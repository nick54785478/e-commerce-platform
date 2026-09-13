package com.omni.recommender.spark;

import com.google.gson.Gson;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.IndexToString;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.StringIndexerModel;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.functions.*;

/**
 * 推薦系統批次運算任務 (Recommender Batch Job)
 * 
 * <p>本程式為 Spark 離線批次運算任務，主要負責執行「協同過濾 (Collaborative Filtering)」推薦演算法。
 * 它會定期由 Apache Airflow 觸發執行，進行以下流程：</p>
 * <ol>
 *   <li><b>讀取資料湖</b>：從 MinIO 中的 Apache Iceberg 表格 (user_behaviors) 讀取歷史使用者行為日誌。</li>
 *   <li><b>特徵工程與評分</b>：將不同的使用者行為（如購買、加入購物車、瀏覽）轉換為隱式反饋 (Implicit Feedback) 的權重分數。</li>
 *   <li><b>模型訓練</b>：使用 Spark MLlib 的 ALS (Alternating Least Squares) 交替最小平方法建立推薦矩陣。</li>
 *   <li><b>產生推薦</b>：為所有使用者產生 Top 10 的專屬推薦商品清單。</li>
 *   <li><b>快取更新</b>：將計算好的推薦清單透過分區寫入 (foreachPartition) 的方式，極速更新至 Redis 中，供線上 API 查詢。</li>
 * </ol>
 */
public class RecommenderBatchJob {

    public static void main(String[] args) {

        // 取得基礎設施的環境變數設定，若無則使用預設的 localhost
        String minioEndpoint = System.getenv("MINIO_ENDPOINT") != null ? System.getenv("MINIO_ENDPOINT") : "http://localhost:9000";
        String redisHost = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : "localhost";

        // 1. 初始化 SparkSession (Spark 叢集的進入點)
        // 這裡特別配置了 Iceberg 擴充元件以及 S3A 檔案系統，讓 Spark 能夠看懂 MinIO 上的 Iceberg 表格格式。
        SparkSession spark = SparkSession.builder()
                .appName("ALSRecoBatchJob")
                .master("local[*]") // 在本地測試時使用所有可用的 CPU 核心，實際上線應由 Spark Submit 決定
                .config("spark.sql.extensions", "org.apache.iceberg.spark.extensions.IcebergSparkSessionExtensions")
                .config("spark.sql.catalog.omni_catalog", "org.apache.iceberg.spark.SparkCatalog")
                .config("spark.sql.catalog.omni_catalog.type", "hadoop")
                .config("spark.sql.catalog.omni_catalog.warehouse", "s3a://ecommerce-data-lake/warehouse")
                .config("spark.hadoop.fs.s3a.endpoint", minioEndpoint)
                .config("spark.hadoop.fs.s3a.access.key", "minioadmin")
                .config("spark.hadoop.fs.s3a.secret.key", "minioadmin")
                .config("spark.hadoop.fs.s3a.path.style.access", "true") // MinIO 必須開啟 path-style access
                .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
                .config("spark.hadoop.fs.s3a.connection.ssl.enabled", "false")
                .getOrCreate();

        // 2. 讀取與過濾資料 (Data Loading & Filtering)
        // 從 Iceberg 資料湖讀取日誌，並濾除掉缺少核心 ID 的髒資料。
        Dataset<Row> behaviorDf = spark.read()
                .format("iceberg")
                .load("omni_catalog.db.user_behaviors")
                .filter(col("tenantId").isNotNull().and(col("tenantId").notEqual("")))
                .filter(col("userId").isNotNull().and(col("userId").notEqual("")))
                .filter(col("itemId").isNotNull().and(col("itemId").notEqual("")));

        // 建立複合 ID 以支援多租戶隔離 (Multi-Tenancy Isolation)
        // 由於不同租戶可能擁有相同的 userId (例如 user-1)，因此將 tenantId 與 userId/itemId 組合在一起，
        // 確保 A 租戶的行為絕對不會影響到 B 租戶的推薦模型。
        Dataset<Row> multiTenantDf = behaviorDf
                .withColumn("tenantUserId", concat(col("tenantId"), lit("_"), col("userId")))
                .withColumn("tenantItemId", concat(col("tenantId"), lit("_"), col("itemId")));

        // 3. 資料前處理：隱式反饋計分 (Implicit Feedback Scoring)
        // 在電商領域，我們通常沒有使用者對商品的 1~5 星明確評分 (Explicit Rating)。
        // 必須將「行為」轉換為「分數」。例如：購買代表強烈意願(5分)，瀏覽只是稍微有興趣(1分)，取消收藏則扣分(-2分)。
        Dataset<Row> scoredDf = multiTenantDf.withColumn("score",
                when(col("behaviorType").equalTo("PURCHASE"), 5.0)
                .when(col("behaviorType").equalTo("FAVORITE"), 4.0)
                .when(col("behaviorType").equalTo("CHECKOUT"), 3.5)
                .when(col("behaviorType").equalTo("ADD_TO_CART"), 3.0)
                .when(col("behaviorType").equalTo("SHARE"), 3.0)
                .when(col("behaviorType").equalTo("RATE"), 2.5)
                .when(col("behaviorType").equalTo("CLICK"), 1.5)
                .when(col("behaviorType").equalTo("VIEW"), 1.0)
                .when(col("behaviorType").equalTo("SEARCH"), 1.0)
                .when(col("behaviorType").equalTo("REMOVE_FROM_CART"), -1.0)
                .when(col("behaviorType").equalTo("UNFAVORITE"), -2.0)
                .otherwise(0.0)
        );

        // 如果同一個使用者對同一個商品互動多次，將分數加總 (聚合)。
        // 過濾掉總分為零或負數的商品（代表使用者不感興趣或討厭該商品）。
        Dataset<Row> aggregatedDf = scoredDf.groupBy("tenantId", "tenantUserId", "tenantItemId")
                .agg(sum("score").alias("total_score"))
                .filter(col("total_score").gt(0.0)); // 只保留正向訊號

        // 4. 特徵工程 (Feature Engineering)
        // Spark 的 ALS 模型只接受 Integer 格式的使用者與商品 ID，不能接受 String (UUID)。
        // 因此使用 StringIndexer 將 String UUID 轉換為 Integer 索引 (Index)。
        StringIndexer userIndexer = new StringIndexer()
                .setInputCol("tenantUserId")
                .setOutputCol("userId_index")
                .setHandleInvalid("skip");
        StringIndexerModel userModel = userIndexer.fit(aggregatedDf);
        Dataset<Row> withUserIndex = userModel.transform(aggregatedDf);

        StringIndexer itemIndexer = new StringIndexer()
                .setInputCol("tenantItemId")
                .setOutputCol("itemId_index")
                .setHandleInvalid("skip");
        StringIndexerModel itemModel = itemIndexer.fit(withUserIndex);
        Dataset<Row> trainingData = itemModel.transform(withUserIndex);

        // 5. 訓練 ALS 模型 (Train ALS Model)
        // ALS (Alternating Least Squares) 是 Spark MLlib 內建的協同過濾演算法。
        ALS als = new ALS()
                .setMaxIter(10)          // 最大迭代次數
                .setRegParam(0.01)       // 正規化參數，防止過擬合 (Overfitting)
                .setUserCol("userId_index")
                .setItemCol("itemId_index")
                .setRatingCol("total_score")
                .setImplicitPrefs(true)  // 關鍵：開啟隱式反饋模式 (處理點擊、瀏覽等非顯性評分)
                .setColdStartStrategy("drop"); // 遇到測試集中的新使用者/新商品時，直接丟棄避免報錯

        // 開始進行矩陣分解，這會耗費大量記憶體與 CPU 運算。
        ALSModel model = als.fit(trainingData);

        // 6. 為所有使用者產生 Top-N 推薦 (Generate Recommendations)
        // 針對訓練集中的每一位使用者，運算出最可能感興趣的 10 件商品。
        Dataset<Row> rawRecs = model.recommendForAllUsers(10);

        // 將產生出來的 Integer 索引，反向對映回原本的 String UUID (包含 TenantID)。
        IndexToString userConverter = new IndexToString()
                .setInputCol("userId_index")
                .setOutputCol("tenantUserId")
                .setLabels(userModel.labels());
        Dataset<Row> recsWithUserId = userConverter.transform(rawRecs);

        String[] itemLabels = itemModel.labels(); // 準備稍後解析 Item ID 的標籤陣列
        
        // 7. 將結果平行寫入 Redis 快取 (Parallel Write to Redis)
        // 使用 foreachPartition 可以確保在 Spark Worker 節點上，每一個分區 (Partition) 
        // 只會建立一個 JedisPool 與連線，避免頻繁建立連線導致網路癱瘓。
        recsWithUserId.foreachPartition(partition -> {
            // 初始化該分區專屬的 Redis 連線池
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            String host = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : "localhost";
            JedisPool jedisPool = new JedisPool(poolConfig, host, 6379, 2000, "ecommerce_redis_pass");
            Gson gson = new Gson();

            try (Jedis jedis = jedisPool.getResource()) {
                while (partition.hasNext()) {
                    Row row = partition.next();
                    String tenantUserId = row.getString(row.fieldIndex("tenantUserId"));
                    
                    // 將 tenantUserId 切割回 tenantId 與 userId
                    String[] tuParts = tenantUserId.split("_", 2);
                    if (tuParts.length < 2) continue;
                    
                    // 移除之前因為 JSON 解析殘留下來的 {"value":"..."} 包裝，確保寫入 Redis 的 Key 乾淨無瑕
                    String tenantId = tuParts[0].replaceAll("\\{\\\"value\\\":\\\"|\\\"\\}", "");
                    String userId = tuParts[1].replaceAll("\\{\\\"value\\\":\\\"|\\\"\\}", "");

                    // 讀取推薦陣列
                    List<Row> recsList = row.getList(row.fieldIndex("recommendations"));
                    List<Map<String, Object>> formattedRecs = new ArrayList<>();
                    
                    for (Row rec : recsList) {
                        int itemIndex = rec.getInt(0); // 取出 itemId_index
                        float score = rec.getFloat(1); // 取出推薦分數
                        
                        // 將 int 索引轉回真實的 tenantItemId
                        if (itemIndex >= 0 && itemIndex < itemLabels.length) {
                            String tenantItemId = itemLabels[itemIndex];
                            String[] tiParts = tenantItemId.split("_", 2);
                            if (tiParts.length >= 2) {
                                // 一樣要移除 JSON 包裝
                                String originalItemId = tiParts[1].replaceAll("\\{\\\"value\\\":\\\"|\\\"\\}", "");
                                
                                // 封裝成 JSON 可接受的 Map 格式
                                Map<String, Object> recMap = new HashMap<>();
                                recMap.put("itemId", originalItemId);
                                recMap.put("score", score);
                                formattedRecs.add(recMap);
                            }
                        }
                    }
                    
                    // 組裝 Redis 的 Key，遵循系統架構中設計的格式
                    String redisKey = "tenant:" + tenantId + ":user:" + userId + ":recs";
                    // 將 List 轉換成 JSON 字串
                    String jsonValue = gson.toJson(formattedRecs);
                    // 寫入 Redis
                    jedis.set(redisKey, jsonValue);
                }
            }
            // 關閉該分區的 Redis 連線池
            jedisPool.close();
        });

        System.out.println("ALS Recommendation batch job finished successfully! Results pushed to Redis.");
        
        // 任務完成，優雅關閉 SparkSession，釋放運算資源。
        spark.stop();
    }
}
