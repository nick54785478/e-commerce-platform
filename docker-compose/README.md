# E-Commerce Platform - 基礎設施容器化部署 (Docker Compose)

本目錄包含了電商平台 (E-Commerce Platform) 運作所需的所有基礎設施服務與中介軟體 (Middleware)。我們採用現代化的 Cloud-Native 架構，拔除舊有的 Hadoop/HBase，全面改用輕量、高效能的替代方案。

## 📁 目錄結構

```text
docker-compose/
├── ecommerce/
│   ├── docker-compose.yml     # 核心基礎設施定義檔
│   └── airflow/               # Airflow 相關目錄 (掛載用)
│       └── dags/              # 排程腳本放置處 (例如：Spark 推薦模型訓練)
└── README.md                  # 本說明文件
```

## 🚀 如何啟動與建置

請確保您的系統已安裝 **Docker** 與 **Docker Compose**，並執行以下步驟啟動所有服務：

1. **進入部署目錄**
   ```bash
   cd ecommerce
   ```

2. **在背景啟動所有容器**
   ```bash
   docker-compose up -d
   ```

3. **檢查容器運行狀態**
   ```bash
   docker-compose ps
   ```

4. **停止並移除所有容器 (若需要)**
   ```bash
   docker-compose down
   ```

5. **完全重置與重啟 (含 Airflow 資料庫初始化)**
   > [!WARNING]
   > 如果您使用 `docker-compose down -v` 清除所有 Volume，Postgres 資料庫會被重置。這將導致 Airflow 的網頁伺服器與調度器因為找不到初始化過的資料表而直接崩潰。請務必依照以下順序重新啟動：

   ```bash
   # 1. 停止並清空所有容器與資料卷
   docker-compose down -v

   # 2. 先單獨啟動 Postgres 並等待其就緒 (約需幾秒鐘)
   docker-compose up -d ecommerce-postgres

   # 3. 執行 Airflow 資料庫初始化 (建立資料表)
   docker-compose run --rm ecommerce-airflow-webserver airflow db migrate

   # 4. 建立 Airflow Admin 帳號 (帳號密碼皆為 admin)
   docker-compose run --rm ecommerce-airflow-webserver airflow users create --role Admin --username admin --password admin --email admin@example.com --firstname admin --lastname admin

   # 5. 啟動所有其餘微服務與中間件
   docker-compose up -d
   ```

---

## 🛠️ 核心服務功能清單與 Port 對照表

透過 `docker-compose up -d` 啟動後，以下服務將在您的本機運行。您可以透過對應的 Port 進行存取或除錯：

### 1. 核心資料儲存 (Data Storage)
| 服務名稱 | 容器名稱 | Port 對應 | 核心功能說明 |
| :--- | :--- | :--- | :--- |
| **PostgreSQL** | `ecommerce-postgres` | `5432` | 系統主要關聯式資料庫 (儲存商品、訂單、使用者等業務狀態)。密碼預設為 `postgres`。 |
| **Redis** | `ecommerce-redis` | `6379` | 分散式快取與網關限流，取代過往的 HBase 儲存即時特徵。需要密碼 (`ecommerce_redis_pass`)。 |
| **MinIO** | `ecommerce-minio` | `9000` (API)<br>`9001` (UI) | S3 相容的物件儲存伺服器，取代 HDFS 用於儲存大數據資料集 (如 Iceberg Table) 與機器學習模型。 |
| **Axon Server** | `ecommerce-axon-server` | `8024` (UI)<br>`8124` (gRPC) | 領域驅動設計 (DDD) 及 Event Sourcing 架構的核心 Event Store 與 Command/Query 訊息路由器。 |

### 2. 訊息佇列 (Message Queue)
| 服務名稱 | 容器名稱 | Port 對應 | 核心功能說明 |
| :--- | :--- | :--- | :--- |
| **Kafka (KRaft)** | `ecommerce-kafka` | `9092` (外部)<br>`29092` (內部) | 系統事件匯流排。無 Zookeeper 的現代 KRaft 模式。雙網監聽設計，供 Spring Boot 微服務與 Spark 讀取。 |
| **Kafka UI** | `ecommerce-kafka-ui` | `8090` | 高顏值的 Kafka 網頁監控面板，用來即時查看 Topic 狀態、訊息流與 Consumer Group。 |

### 3. 大數據運算與排程 (Big Data & Scheduling)
| 服務名稱 | 容器名稱 | Port 對應 | 核心功能說明 |
| :--- | :--- | :--- | :--- |
| **Spark Master** | `ecommerce-spark-master` | `8080` (UI)<br>`7077` (RPC) | 大數據運算的大腦，負責協調整個 Spark 叢集的資源分配 (如 ALS 模型運算)。 |
| **Spark Worker** | `ecommerce-spark-worker` | `8081` (UI) | 實際執行 Spark 運算的節點，啟動後會自動註冊至 Spark Master。 |
| **Airflow Web** | `ecommerce-airflow-webserver` | `8082` | 大數據任務的排程視覺化控制台，可手動觸發 DAG 或查看執行 Log。 |
| **Airflow Sched**| `ecommerce-airflow-scheduler` | `無` | Airflow 背景守護進程，負責解析 `dags/` 目錄下的 Python 腳本並準時派發任務給執行器。 |

---

## 💡 注意事項
1. **防止自動啟動**：為了避免拖垮您的本機資源，所有的容器都設定了 `restart: "no"`。這代表即使您重啟電腦或 Docker Desktop，這些基礎設施都不會自動啟動，您需要透過指令手動啟動。
2. **Airflow DAG 掛載**：如果您有新的排程任務 (如 `docker_spark_recommender_dag.py`)，請確保放置於 `ecommerce/airflow/dags` 目錄下，Airflow 會自動掃描並載入。
