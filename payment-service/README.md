# Payment Service (付款微服務)

這是一個基於 **Axon Framework** 實作，專門負責電商平台「金流串接與付款狀態維護」的微服務。
本專案透過事件溯源 (Event Sourcing) 與領域驅動設計 (DDD)，確保在分散式交易中，金流狀態的轉換具有強一致性與防禦性，並完美融入訂單的 Saga 協調流程中。

**技術棧與基礎設施**

| 分類 | 技術選型 | 職責說明 |
| --- | --- | --- |
| 核心框架 | Axon Framework | 提供 Command/Event Bus 與 Event Sourcing 支援 |
| 持久化 (寫) | Axon Server | 專用的 Event Store，存儲不可變的金流變更事實 |
| API 定義 | shared-apis (`payment-api`) | 跨服務共用的 Domain Commands 與 Events |

---

## 核心架構：領域模型 (Domain Aggregate)

專案內僅包含單一核心聚合根：

* **`Payment` (付款紀錄)**：
  專注於管理一筆交易的金流狀態機 (包含：建立、處理完成、取消、退款)。
  內部實作了防禦性設計 (Defensive Design)，例如在處理付款時強制比對**傳入金額**與**應付金額**，防止客戶端惡意篡改。

---

## 金流狀態機與 Saga 協調流程

本服務完全相容於 `order-service` 的 `OrderManagementSaga`，負責處理流程中的金流環節：

1. **建立付款 (Create Payment)**
   * **指令**: `CreatePaymentCommand`
   * **情境**: 訂單建立且「庫存鎖定成功」後，Saga 會自動向本服務發出建立付款的指令。
   * **邏輯**: 初始化付款紀錄並綁定 `orderId`，發佈 `PaymentCreatedEvent`，狀態標記為「待付款」。

2. **執行扣款 (Process Payment)**
   * **指令**: `ProcessPaymentCommand`
   * **情境**: 使用者於前端填寫信用卡資訊並送出結帳時 (目前實作為自動模擬成功)。
   * **邏輯**: 驗證付款狀態必須是「未付款」，並校驗前端傳入的 `amount` 是否等於建立時的金額，避免竄改。驗證通過後，發佈 `PaymentProcessedEvent`。這會觸發 Saga 去執行「庫存確認扣減」與後續的出貨通知。

3. **取消付款 (Cancel Payment)**
   * **指令**: `CancelPaymentCommand`
   * **情境**: 訂單在 10 分鐘內未完成付款 (超時)，或是使用者主動取消訂單。
   * **邏輯**: 狀態標記為「已取消」，發佈 `PaymentCancelledEvent`，終止該筆金流的操作。

4. **執行退款 (Refund Payment)**
   * **指令**: `RefundPaymentCommand`
   * **情境**: 訂單「已付款」但遭遇意外 (如實體庫存異常)，或消費者事後要求退貨退款。
   * **邏輯**: 發佈 `PaymentRefundedEvent`，模擬將款項退回給消費者。

---

## 快速開始

1. 確保 Axon Server 正常運行 (可透過根目錄的 docker-compose 建立容器)。
2. 本微服務無須外部的關聯式資料庫 (其狀態由 Axon Server 中的 Event Store 重建)。
3. 確保 `shared-apis` 已經成功 `mvn install` 編譯。
4. 啟動 Spring Boot 應用程式即可開始服務。
