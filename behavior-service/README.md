# User Behavior Service (用戶行為採集服務)

本微服務是 OmniStore 電商平台中的**高併發資料採集閘道口**，專門負責接收來自前端 (Web/App) 以及跨服務 (如 `product-service` 的瀏覽觸發) 的所有使用者行為日誌。

## 🌟 模組定位與設計亮點

* **極致輕量與無狀態 (Stateless)**：為了應對如雙 11 等大促銷時的海量點擊與瀏覽請求，本服務內部**不連接任何關聯式資料庫 (No SQL DB)**。它是一個純粹的無狀態服務，可以非常輕易地透過 Kubernetes 進行橫向擴展 (Scale-out)。
* **事件驅動架構 (Event-Driven via Kafka)**：捨棄傳統的「寫入本地端日誌檔案」做法。所有的行為請求在通過簡單的驗證與 DTO 轉換後，會被立即封裝成 JSON 訊息，並以 "Fire and Forget" (射後不理) 的超低延遲模式直接推送至 **Apache Kafka** (`user-behavior-events` Topic)。
* **多租戶支援 (Multi-Tenancy)**：每一個行為事件都會嚴格綁定 `tenantId` (透過 JWT 或 HTTP Header 傳入)，確保後續大數據運算時的資料隔離。
* **CQRS 領域模型架構**：服務內部依然遵循乾淨架構 (Clean Architecture)，透過 `BehaviorApplicationService` 作為防腐層 (Anti-Corruption Layer)，並將基礎設施細節封裝在 Port & Adapter 中。

---

## 🛠️ 核心功能 (Features)

* **行為打點 API (`/api/v1/behaviors/log`)**：
  接收標準化的行為事件，支援以下多種意圖追蹤：
  * `VIEW` (瀏覽商品)
  * `CLICK` (點擊商品)
  * `SEARCH` (搜尋)
  * `FAVORITE` / `UNFAVORITE` (加入/取消最愛)
  * `ADD_TO_CART` / `REMOVE_FROM_CART` (加入/移出購物車)
  * `CHECKOUT` (準備結帳)
  * `PURCHASE` (成功購買)
  * `RATE` / `SHARE` (評分/分享)

---

## 🚀 與大數據生態系的互動 (Data Pipeline)

`behavior-service` 在整個現代資料湖倉 (Modern Data Lakehouse) 架構中扮演著**資料生產者 (Producer)** 的角色：

1. **前端 / API Gateway** 發送使用者的行為日誌 (`POST /api/v1/behaviors/log`)。
2. **Behavior Service** 將其轉換為領域層的 Event，並直接推入 **Kafka** (`user-behavior-events`)。
3. **KafkaToIcebergIngestionJob (Spark Streaming)** (位於 `spark-recommender` 模組) 扮演消費者，即時從 Kafka 拉取這些日誌，並落地寫入 **MinIO** 上的 **Apache Iceberg** 表格。
4. **RecommenderBatchJob (Spark Batch)** 每天/每小時定期讀取 Iceberg 的歷史資料，訓練 ALS 協同過濾模型，並將結果快取至 Redis 提供推薦。

---

## ⚙️ 環境變數與配置

執行此服務前，需確保環境變數或 `application.yml` 已配置正確的 Kafka 連線：

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092  # Kafka Broker 位址
```

*(註：本服務過去曾依賴 Hadoop HDFS 進行微批次檔案上傳，現已全面重構升級為 Kafka 即時串流架構，徹底解決了磁碟 I/O 瓶頸。)*
