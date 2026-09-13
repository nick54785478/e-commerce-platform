package com.omni.recommender.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

/**
 * Kafka 串流資料注入 Iceberg 任務 (Kafka to Iceberg Ingestion Job)
 * 
 * <p>本程式為 Spark Structured Streaming 長駐型串流運算任務。它是現代資料湖倉 (Data Lakehouse) 架構中的關鍵組件。</p>
 * <p>主要負責以下流程：</p>
 * <ol>
 *   <li><b>訂閱 Kafka</b>：即時監聽來自 `user-behavior-events` Topic 的行為日誌訊息 (由 User Behavior Service 非同步發送)。</li>
 *   <li><b>資料解析</b>：將 Kafka 裡面的 JSON 原始字串，對應並解析為 Spark DataFrame 的 Schema。</li>
 *   <li><b>落地湖倉 (Ingestion)</b>：將解析後的資料即時 (每分鐘微批次) 寫入 MinIO 中的 Apache Iceberg 表格中。</li>
 * </ol>
 * <p>透過此機制，所有前端使用者的行為資料可以在極低延遲的情況下，被安全且結構化地保存到 Iceberg 供未來的機器學習任務使用。</p>
 */
public class KafkaToIcebergIngestionJob {

    public static void main(String[] args) throws TimeoutException, StreamingQueryException {

        // 取得基礎設施的環境變數，包含 MinIO 物件儲存與 Kafka 的連線位置
        String minioEndpoint = System.getenv("MINIO_ENDPOINT") != null ? System.getenv("MINIO_ENDPOINT") : "http://localhost:9000";
        String kafkaBootstrap = System.getenv("KAFKA_BOOTSTRAP_SERVERS") != null ? System.getenv("KAFKA_BOOTSTRAP_SERVERS") : "localhost:9092";

        // 1. 初始化 Spark Session (配置 Iceberg 與 MinIO S3A)
        // 與批次任務一樣，需要載入 Iceberg 相關擴充與 S3A 認證資訊。
        SparkSession spark = SparkSession.builder()
                .appName("KafkaToIcebergIngestion")
                .master("local[*]") // 在開發測試環境本地執行
                
                // --- Iceberg Catalog 設定 ---
                // 使用 Hadoop Catalog 作為 Iceberg 的中繼資料庫，並將倉庫指向 MinIO 的 S3A 路徑。
                .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
                .config("spark.sql.catalog.omni_catalog", "org.apache.iceberg.spark.SparkCatalog")
                .config("spark.sql.catalog.omni_catalog.type", "hadoop")
                .config("spark.sql.catalog.omni_catalog.warehouse", "s3a://ecommerce-data-lake/warehouse")
                
                // --- MinIO (S3A) 設定 ---
                // 此處的帳號密碼對應 docker-compose 內部的 MinIO 配置。
                .config("spark.hadoop.fs.s3a.endpoint", minioEndpoint)
                .config("spark.hadoop.fs.s3a.access.key", "minioadmin")
                .config("spark.hadoop.fs.s3a.secret.key", "minioadmin")
                .config("spark.hadoop.fs.s3a.path.style.access", "true")
                .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false")
                
                .getOrCreate();

        // 2. 定義 JSON 負載的 Schema (Data Schema Definition)
        // 為了將無結構的 JSON 轉換為結構化的表，我們必須明確定義各欄位的型別。
        // 這邊的結構必須與 User Behavior Service 送出的 Event DTO 一模一樣。
        StructType deviceInfoSchema = new StructType()
                .add("clientIp", DataTypes.StringType)
                .add("userAgent", DataTypes.StringType);

        StructType schema = new StructType()
                .add("behaviorId", DataTypes.StringType)
                .add("tenantId", DataTypes.StringType)
                .add("userId", DataTypes.StringType)
                .add("sessionId", DataTypes.StringType)
                .add("itemId", DataTypes.StringType)
                .add("behaviorType", DataTypes.StringType)
                .add("referrerUrl", DataTypes.StringType)
                .add("timestamp", DataTypes.StringType) // 因 Jackson 預設行為，接收到的時間是字串
                .add("deviceInfo", deviceInfoSchema);

        // 3. 從 Kafka 讀取資料串流 (Read Stream from Kafka)
        Dataset<Row> kafkaStream = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaBootstrap)
                .option("subscribe", "user-behavior-events")
                .option("startingOffsets", "earliest") // 若發生重啟，從未處理過的最早紀錄開始讀取 (保證不掉資料)
                .load();

        // 4. 解析 JSON 並轉換 DataFrame (Parse and Transform)
        Dataset<Row> behaviorDf = kafkaStream
                // Kafka 進來的訊息預設是 binary 格式，先轉型為 String。
                .selectExpr("CAST(value AS STRING) as json_payload")
                // 透過 from_json 函式與前面定義好的 Schema，將字串解析成欄位。
                .select(from_json(col("json_payload"), schema).as("data"))
                .select("data.*")
                // 將 timestamp 字串轉換為真實的 SQL Timestamp 型別。
                // 這是為了後續 Iceberg 能夠進行時間分區 (Time Partitioning) 與時間旅行查詢 (Time Travel)。
                .withColumn("event_time", to_timestamp(col("timestamp")));

        // 5. 初始化 Iceberg 資料庫與表格 (Initialize Iceberg Namespace & Table)
        spark.sql("CREATE NAMESPACE IF NOT EXISTS omni_catalog.db");
        
        try {
            // 嘗試讀取表格，如果存在代表已經初始化過了
            spark.table("omni_catalog.db.user_behaviors");
        } catch (Exception e) {
            System.out.println("Iceberg table does not exist. Creating it...");
            // 如果報錯，則透過建立一個結構相同但全空的 DataFrame，並儲存為 Iceberg 格式，來完成表格的初始化建立。
            spark.createDataFrame(new java.util.ArrayList<Row>(), schema)
                 .withColumn("event_time", lit(null).cast("timestamp"))
                 .write()
                 .format("iceberg")
                 .mode("append")
                 .saveAsTable("omni_catalog.db.user_behaviors");
        }

        // 6. 將串流資料寫入 Iceberg (Write Stream to Iceberg)
        StreamingQuery query = behaviorDf
                .writeStream()
                .format("iceberg")
                .outputMode("append") // 一直往表內追加新日誌
                // 每 1 分鐘觸發一次微批次 (Micro-batch) 寫入。
                // 這樣可以避免每秒鐘產生大量的小檔案 (Small Files Problem)，影響 MinIO 與 Iceberg 效能。
                .trigger(Trigger.ProcessingTime("1 minute"))
                // 設定 Checkpoint，Spark 會在這裡記錄目前 Kafka 已經讀取到哪一個 Offset，
                // 就算任務當機，重啟後也能精準接續，達成 Exactly-Once 語意保證。
                .option("checkpointLocation", "s3a://ecommerce-data-lake/checkpoints/behavior_ingestion")
                .option("path", "omni_catalog.db.user_behaviors")
                .start();

        System.out.println("Streaming job started! Listening to Kafka topic: user-behavior-events");
        
        // 7. 測試模式支援 (Test Mode Handling)
        // 在 E2E 測試腳本中，我們會帶入 TEST_MODE=true，讓程式跑個 70 秒後自動終止，避免卡住測試流程。
        String testMode = System.getenv("TEST_MODE");
        if ("true".equalsIgnoreCase(testMode)) {
            System.out.println("TEST_MODE is enabled. The job will run for 70 seconds and then terminate.");
            query.awaitTermination(70000); // 等待 70 秒
            query.stop();
            System.out.println("TEST_MODE: Streaming job stopped.");
        } else {
            // 正式上線時，程式會永久掛著監聽 Kafka
            query.awaitTermination();
        }
        
        System.out.println("Stopping Spark context...");
        spark.stop();
        System.out.println("Spark context stopped gracefully.");
    }
}
