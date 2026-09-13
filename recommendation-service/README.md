# Recommendation Service (個人化推薦服務)

本微服務是 OmniStore 電商大數據推薦生態系中的**最後一哩路 (Last Mile)**。負責接收前端的請求，並以亞毫秒級的延遲，回傳專屬該使用者的個人化商品推薦清單。

## 🌟 模組定位與設計亮點

* **極致讀取效能 (Ultra-Low Latency)**：為了不拖累前端渲染與使用者體驗，本服務**完全不與關聯式資料庫 (RDBMS) 或龐大的資料湖打交道**。它是一支輕量級的 API 服務，唯一仰賴的資料源就是記憶體快取 (Redis)。
* **職責分離 (Separation of Concerns)**：
  * **重度運算 (Heavy Lifting)**：交給 `spark-recommender` (Apache Spark) 在背景執行 ALS (協同過濾) 矩陣分解，並非同步寫入 Redis。
  * **線上服務 (Online Serving)**：交由本服務 (`recommendation-service`) 處理高併發的讀取請求。
* **嚴謹的多租戶隔離 (Multi-Tenancy)**：推薦清單的查詢必須綁定 `tenantId` (通常由 API Gateway 驗證後透過 Header 帶入)，完全杜絕 A 店家看到 B 店家推薦結果的可能。

---

## 🛠️ 核心功能與資料流

1. **取得推薦 API (`GET /api/v1/recommendations`)**：
   * 前端呼叫此 API，帶上 `userId` 與 `tenantId` (Header)。
   * 服務將其組合成特定的 Redis Key：`tenant:{tenantId}:user:{userId}:recs`。
   * 從 Redis 取出預先算好且格式化為 JSON 的推薦商品 ID 清單與分數。
   * (視架構而定，可直接回傳 ID 陣列，或透過 gRPC/REST 呼叫 `product-service` 補齊商品詳細圖文再回傳)。

---

## 🚀 與大數據生態系的互動 (Data Pipeline)

在整個現代資料湖倉 (Modern Data Lakehouse) 架構中，本服務扮演著**資料消費者 (Consumer)** 的角色：

1. `behavior-service` 負責接收點擊並送到 Kafka。
2. `spark-recommender` 從 Kafka 落地資料到 Iceberg，定期訓練模型，並將 `List<Recommendation>` 寫入 **Redis**。
3. **Recommendation Service** 隨時守候，當前端請求抵達時，直接從 **Redis** 提取最新結果。

---

## ⚙️ 環境變數與配置

執行此服務前，需確保環境變數或 `application.yml` 已配置正確的 Redis 連線：

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: ecommerce_redis_pass
```

*(註：本服務過去曾設計有複雜的 Controller 甚至依賴本地計算，現已全面重構為基於 Redis 的高性能薄層 (Thin Layer)，大幅降低了系統的維運成本與資源消耗。)*
