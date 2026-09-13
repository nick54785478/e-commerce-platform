# Spark Recommender (大數據推薦引擎模組)

本模組是 Omni-Recommender Platform 的大數據核心引擎，負責處理海量的使用者行為日誌，並結合 **現代資料湖倉 (Modern Data Lakehouse)** 架構，訓練機器學習模型以產生個人化推薦。

## 🌟 架構亮點
* **Data Lakehouse 架構**：採用 **Apache Iceberg** 作為資料表格式 (Table Format)，並儲存於 **MinIO** (S3 相容物件儲存)，取代了笨重的傳統 Hadoop/HDFS。
* **即時事件串流**：採用 **Apache Spark Structured Streaming** 即時監聽 Kafka 事件，達成極低延遲的資料落地。
* **多租戶隔離 (Multi-Tenancy)**：底層透過複合鍵 (Composite Key `tenantId_userId`) 確保模型訓練階段各租戶的資料與推薦結果絕對隔離。
* **極速快取**：推薦清單計算完畢後，直接平行寫入 **Redis**，讓線上推薦服務 (`recommendation-service`) 能在亞毫秒級別內回應前端請求。

---

## 🛠️ 核心任務 (Spark Jobs)

本專案主要包含兩支核心的 Spark 應用程式：

### 1. `KafkaToIcebergIngestionJob` (串流落地任務)
* **模式**：Spark Structured Streaming (長駐型任務)
* **職責**：
  * 訂閱 Kafka 的 `user-behavior-events` Topic。
  * 將 JSON 格式的行為日誌解析為結構化 Schema。
  * 將 timestamp 轉換為 SQL Timestamp 以支援 Iceberg 時間旅行。
  * 每分鐘透過微批次 (Micro-batch) 將資料寫入 MinIO 中的 Iceberg 表格 (`omni_catalog.db.user_behaviors`)。
  * 支援 Checkpoint 確保異常重啟時不掉資料 (Exactly-Once)。

### 2. `RecommenderBatchJob` (ALS 模型訓練任務)
* **模式**：Spark Batch (排程型任務，通常由 Airflow 每日/每小時觸發)
* **職責**：
  * 讀取 Iceberg 表中的歷史使用者行為日誌。
  * 進行特徵工程：將 String 類型的 UUID (加上 Tenant 隔離後) 轉換為 Integer 索引。
  * 將不同的行為 (PURCHASE, ADD_TO_CART, VIEW 等) 依照業務權重轉換為**隱式反饋 (Implicit Feedback)** 分數。
  * 訓練 **ALS (Alternating Least Squares)** 協同過濾模型。
  * 產生 Top-N 推薦清單。
  * 透過 `foreachPartition` 平行初始化 JedisPool，將結果 (JSON) 推送至 Redis。

---

## 🚀 執行與測試

### 本機開發環境執行
在執行這些 Spark Job 之前，請確保以下基礎設施已透過 `docker-compose` 啟動：
* MinIO (`http://localhost:9000`)
* Kafka (`localhost:9092`)
* Redis (`localhost:6379`)

您可以直接在 IDE (如 IntelliJ IDEA) 中執行這兩支 Java 程式的 `main` 方法，程式預設會使用 `local[*]` 模式執行。

### 透過 Maven 在 Docker 內執行 (End-to-End 測試)
在根目錄的端到端測試腳本 (`e2e-test.ps1`) 中，我們使用官方 Maven 容器來動態編譯並執行 Spark 任務：

```powershell
# 執行 Ingestion Job (需設定 TEST_MODE=true 以便自動停止)
docker run --rm -v $(pwd):/usr/src/app ... maven:3.9.6-eclipse-temurin-21 mvn compile exec:java "-Dexec.mainClass=com.omni.recommender.spark.KafkaToIcebergIngestionJob"

# 執行 Batch Recommender Job
docker run --rm -v $(pwd):/usr/src/app ... maven:3.9.6-eclipse-temurin-21 mvn compile exec:java "-Dexec.mainClass=com.omni.recommender.spark.RecommenderBatchJob"
```

## 📦 技術堆疊

* **Apache Spark 3.5** (Core, SQL, MLlib, Streaming)
* **Apache Iceberg** (Data Lakehouse Format)
* **Hadoop AWS / S3A** (用於與 MinIO 溝通)
* **Redis Jedis** (快取寫入)
* **Jackson / Gson** (JSON 解析)
