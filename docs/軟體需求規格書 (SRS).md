# 軟體需求規格書 (SRS)：SaaS 多租戶泛用型電商與推薦平台

## 1. 簡介 (Introduction)

### 1.1 系統目的 (Purpose)
本系統旨在打造一套現代化、支援 **多租戶 (Multi-Tenancy)** 的 SaaS 電商平台。不僅提供泛用型的電商核心能力（商品、訂單、庫存管理），更深度整合「事件驅動 (Event-Driven)」架構，具備即時用戶行為蒐集與個人化推薦能力。開發團隊可透過本規格書了解系統的領域邊界、各微服務的職責劃分，以及跨服務間的通訊與協作模式 (如 Saga Pattern)。

### 1.2 背景與範圍 (Scope)
*   **平台轉型**：泛用型電商 SaaS，支援多種產業的商品上架與交易。前端採用 Angular 18 與 PrimeNG 構建。
*   **多租戶架構**：系統原生支援租戶隔離 (Tenant Isolation)，確保各店家的數據安全與獨立運作。
*   **事件驅動與 DDD**：全面採用領域驅動設計 (DDD)、CQRS 架構與事件溯源 (Event Sourcing) 模式。以 Apache Kafka 取代傳統的 API 輪詢與批次寫入，實現微服務間的非同步解耦。
*   **現代資料湖倉**：轉向輕量級、S3 相容的 MinIO 與 Redis，作為資料儲存與極速快取層。

---

## 2. 領域驅動設計：限界上下文與微服務規劃 (Bounded Contexts)

基於 Domain-Driven Design (DDD)，系統劃分為以下核心微服務，並確保跨租戶的資料隔離：

### 2.1 API 網關與租戶上下文 (API Gateway)
*   **職責**：管理 SaaS 租戶的註冊與配置。對外負責統一的路由轉發與 JWT Token 驗證。網關負責解析 JWT 並提取 `Tenant-ID` 等資訊傳遞給後方微服務。

### 2.2 商品上下文 (Product Service)
*   **職責**：多租戶環境下的商品上架、下架、編輯名稱與價格，以及圖片儲存 (MinIO)。
*   **特性**：高頻率讀取，重度依賴 Redis 快取，並作為推薦系統的基礎資料來源 (Content-based Metadata)。

### 2.3 庫存上下文 (Inventory Service)
*   **職責**：精準計算與鎖定商品庫存，防止超賣。處理訂單成立時的預扣 (Reservation) 與取消時的歸還 (Compensation)。

### 2.4 訂單上下文 (Order Service)
*   **職責**：處理購物車、結帳流程與訂單狀態機 (`CREATED`, `APPROVED`, `SHIPPED`, `CANCELLED`)。透過 Saga 協調跨服務交易。
*   **特性**：高強度的資料一致性要求 (ACID Transaction)，獨立於商品服務以避免業務耦合。

### 2.5 金流上下文 (Payment Service)
*   **職責**：處理金流意圖與第三方支付串接，發布付款成功或退款事件。

### 2.6 用戶行為上下文 (User Behavior Service)
*   **職責**：接收極高併發的前端行為打點請求（瀏覽、點擊、加入購物車）。
*   **特性**：無狀態且極輕量，將行為數據轉換為標準化事件後，推入 Kafka Topic 供下游實時運算與歸檔。

### 2.7 推薦引擎上下文 (Recommendation Service)
*   **職責**：為終端用戶提供即時的客製化商品推薦清單。
*   **特性**：從 Redis 讀取快取，並透過消費 Kafka 行為事件解決冷啟動問題。

### 2.8 物流與出貨上下文 (Shipment Service) - *未來擴充*
*   **職責**：管理訂單出貨、物流單號產生、第三方物流公司串接 (如 FedEx、超商取貨) 及貨態追蹤。
*   **特性**：非同步作業。接收到付款成功及訂單準備出貨事件後，進行後續的實體物流處理。與庫存服務的職責完全分離。

---

## 3. 系統架構設計 (System Architecture)

> [!NOTE]
> 本系統採用雲端原生 (Cloud-Native) 的微服務與事件驅動架構。

*   **API 應用層 (Java 22 + Spring Boot)**:
    *   `Spring Cloud Gateway`：微服務統一入口。
    *   後端微服務負責處理對應領域的 HTTP 請求，遵循 Clean Architecture 隔離領域邏輯。
*   **事件匯流排與微服務通訊 (Event Bus & Routing)**:
    *   `Apache Kafka (KRaft)`：負責跨系統的巨量非同步通訊 (如前端行為日誌串流)。
    *   `Axon Server`：專為 CQRS 與 Event Sourcing 設計，負責處理微服務之間的 Command 路由與 Saga 流程協調。
*   **資料儲存與大數據運算層 (Storage, Data Lakehouse & Compute)**:
    *   `PostgreSQL`：核心關聯式資料庫，儲存高一致性資料，兼作 Iceberg 的 JDBC Catalog。
    *   `Redis`：分散式快取、API 網關限流及推薦清單 Read Model。
    *   `MinIO`：S3 相容物件儲存，儲存 Parquet 格式的 Iceberg 資料表與靜態圖片。
    *   `Apache Spark`：大數據運算引擎，結合 Iceberg 進行資料清洗與推薦模型 (ALS) 訓練。

---

## 4. 多租戶 (Multi-Tenancy) 策略

*   **資料隔離層級**：採用 **Shared Database, Shared Schema** 的架構。所有資料表皆具備 `tenant_id` 欄位。透過 Hibernate/JPA 的 `@Filter` 強制在底層加上租戶過濾條件。
*   **租戶配置**：支援不同租戶自訂部分商業邏輯，例如：自訂商品分類階層、自訂推薦演算法的權重偏好。

---

## 5. 功能需求 (Functional Requirements)

### 5.1 租戶與商品管理
*   **FR-1.1**: 系統需支援多租戶註冊與網域綁定。
*   **FR-1.2**: 支援商品上架、下架、編輯名稱、價格與圖片。圖片上傳至 MinIO，提供對外公開的存取 URL。
*   **FR-1.3**: 支援關鍵字搜尋與分頁瀏覽商品列表。

### 5.2 庫存管理
*   **FR-2.1 庫存預扣 (Reservation)**：當訂單成立時，需即時預扣商品庫存。
*   **FR-2.2 庫存歸還 (Compensation)**：當訂單取消或退款時，需將預扣的庫存歸還。
*   **FR-2.3 庫存查詢**：提供即時的可售庫存數量。

### 5.3 交易與訂單處理
*   **FR-3.1 訂單建立**：使用者可跨裝置將商品加入購物車，並成立訂單。
*   **FR-3.2 分散式交易協調 (Saga)**：
    * 當訂單建立時，觸發扣減庫存指令。
    * 庫存扣減成功後，觸發建立付款指令。
    * 付款成功後，更新狀態為準備出貨。
    * 若付款逾時或扣庫存失敗，觸發補償機制，取消訂單並歸還庫存。

### 5.4 金流處理
*   **FR-4.1 建立支付意圖**：接受來自 Saga 的指令，建立一筆待支付紀錄。
*   **FR-4.2 支付回報與退款**：處理付款與退貨，發布成功或退款事件 (`PaymentProcessedEvent`)。

### 5.5 數據採集與推薦
*   **FR-5.1**: 行為採集服務需提供毫秒級 API，穩定將事件推入 Kafka。
*   **FR-5.2**: 推薦服務需實時更新用戶畫像，並從 Redis 返回 Top-N 推薦。缺乏歷史資料的匿名使用者，需提供「熱門商品」降級 (Fallback) 策略。

### 5.6 物流與出貨處理 (未來擴充)
*   **FR-6.1 物流單建立**：接收出貨通知後，向第三方物流系統請求產生託運單號。
*   **FR-6.2 貨態追蹤**：提供 API 供前端查詢包裹的即時配送進度。

---

## 6. 非功能需求 (Non-Functional Requirements)

*   **架構模式 (CQRS)**：後端嚴格遵守 CQRS 架構，命令與查詢模型分離。
*   **事件驅動與最終一致性**：微服務間的狀態變更需透過事件溯源發布，不得使用同步 API 進行改變狀態的跨服務呼叫。Saga 保證異常發生時的最終一致性 (Eventual Consistency)。
*   **效能 (Performance)**: Recommendation API 響應時間 P95 < 50ms；User Behavior API 響應時間 P99 < 20ms。
*   **可擴展性 (Scalability)**: 微服務必須是無狀態 (Stateless)，能輕易擴展。
*   **安全性 (Security)**: MinIO 儲存桶需配置為唯讀公開以供渲染。所有請求需經過 API 網關進行 JWT 認證，透過內部 Header `X-Tenant-ID` 轉發確保資料不越界。

---

## 7. 技術堆疊 (Technology Stack)

*   **Frontend**: Angular 18, PrimeNG
*   **Backend Framework**: Java 22, Spring Boot 3.3.x
*   **Microservices & DDD**: Spring Cloud Gateway, Axon Framework (CQRS & Saga)
*   **Data Access**: Spring Data JPA, Hibernate (Tenant ID Filter)
*   **Infrastructure** (Dockerized):
    *   PostgreSQL 15+ (Relational DB & Iceberg Catalog)
    *   Redis 7.x (Cache)
    *   Apache Kafka 7.5+ (Event Streaming)
    *   Axon Server (Command & Event Routing)
    *   MinIO (S3 Compatible Storage)
    *   Apache Spark 3.5 & Apache Iceberg (Data Lakehouse Engine & Table Format)
