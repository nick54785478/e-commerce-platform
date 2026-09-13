# Order Service (訂單與購物車微服務)

這是一個基於 Axon Framework 實作的高性能、彈性訂單管理系統。本專案採用事件溯源 (Event Sourcing) 與 CQRS 模式構建，並負責擔任電商交易的「交響樂團指揮 (Orchestrator)」，透過 **Saga 模式** 協調跨微服務的複雜商業流程。

**技術棧與基礎設施**

| 分類 | 技術選型 | 職責說明 |
| --- | --- | --- |
| 核心框架 | Axon Framework | 提供 Command/Event/Query Bus 與 Saga 生命週期管理 | 
| 微服務通訊 | Spring HttpInterface | 透過宣告式 REST Client 呼叫 `product-service` 取得商品資訊 |
| 資料轉換 | MapStruct | 實作防腐層，確保 Domain 與 API 契約解耦 | 
| 持久化 (寫) | Axon Server | 專用的 Event Store，存儲不可變的事實 (Facts) | 
| 持久化 (讀) | MySQL + JPA | 針對查詢優化的投影模型 (Read Model) | 

---

## 核心架構：領域模型 (Domain Aggregates)

專案遵循整潔架構原則，確保業務邏輯與技術細節隔離。

* **Order Aggregate (訂單)**：管理訂單狀態機（建立、取消、完結）。
* **CartItem Aggregate (購物車)**：管理使用者的購物車內容與數量。

*(註：商品 (Product)、庫存 (Inventory) 與支付 (Payment) 已抽離至各自獨立的微服務中)*

---

## Saga 業務流程設計：OrderManagementSaga

本系統採用 **「協調型 Saga (Orchestration)」**，將訂單建立與支付執行解耦，支援手動觸發支付與自動超時保護，並確保分散式環境下的最終一致性。

### 標準成功路徑 (Happy Path)：

**1. 下單**：用戶發送 `CreateOrderCommand`，Order Aggregate 建立訂單並發佈 `OrderCreatedEvent`。
**2. 庫存預留**：Saga 監聽到訂單建立，發送 `ReserveStockCommand` 給 `inventory-service`。
**3. 支付準備**：庫存鎖定成功後，Saga 發送 `CreatePaymentCommand` 給 `payment-service` 並開啟 10 分鐘倒數 (Deadline)。
**4. 執行扣款**：用戶觸發支付，`payment-service` 執行付款處理。
**5. 確認庫存**：Saga 收到 `PaymentProcessedEvent`，通知 `inventory-service` 執行 `ConfirmStockReservationCommand` (正式扣除庫存)。
**6. 流程終點**：Saga 觸發出貨通知並結束生命週期。

### 精確補償與超時機制 (Resilience & Compensation)

* **自動超時保護**：若用戶在 10 分鐘內未完成支付，Axon `DeadlineManager` 觸發 `handlePaymentTimeout`，Saga 會自動發送 `CancelOrderCommand`。
* **庫存不足 / 鎖定失敗**：若庫存預留失敗，Saga 將立刻取消訂單。
* **退款與庫存釋放**：
  Saga 具備**狀態感知能力**，當訂單被取消時：
  * 若未付款：僅發送 `CancelPaymentCommand` 取消交易，並發送 `CancelStockReservationCommand` 釋放被鎖定的庫存。
  * 若已付款 (如取消訂單或退貨)：觸發 `RefundPaymentCommand` 進行退款，並發送 `AddStockCommand` 將實體庫存歸還。

---

## 跨服務資料串接 (HttpInterface)

在購物車的操作中 (加入商品、結帳)，系統需要即時獲取商品的最新價格與上架狀態。
`order-service` 使用了 Spring Boot 3 內建的 **HTTP Interfaces** (`ProductApiClient`)，將對 `product-service` 的 REST API 呼叫抽象化為 Java 介面，並透過 Header 傳遞 `X-Tenant-ID` 支援多租戶架構。

## 快速開始

1. 啟動 Axon Server、MySQL (可透過根目錄的 docker-compose 建立容器)。
2. 配置 MySQL `order_view` 等表結構。
3. 確保 `product-service`、`inventory-service` 及 `payment-service` 正常運行。
4. 啟動 Spring Boot 應用程式即可開始服務。
