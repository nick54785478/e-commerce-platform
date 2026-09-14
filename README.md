# E-Commerce Platform (Modern Data Lakehouse Edition) 🚀

這是從原本的 Omni-Recommender Platform 分支出來的 **全新 V2 架構** 儲存庫。

## 🌟 架構願景 (Architecture Vision)

我們正在進行「大數據基礎設施的雲端原生轉型」。本專案將放棄傳統笨重的 Hadoop / HBase 架構，全面轉向事件驅動與湖倉一體化 (Modern Data Lakehouse) 架構：

1. **事件驅動 (Event-Driven)**: 引入 **Apache Kafka**，將微批次檔案寫入升級為即時串流處理。
2. **現代資料湖倉 (Modern Data Lakehouse)**: 拔除 HDFS，改用輕量級、S3 相容的 **MinIO**，並搭配 **Apache Iceberg** 實作具備 ACID 交易能力的資料湖倉。
3. **極速快取 (Ultra-Fast Cache)**: 以純記憶體的 **Redis** 取代笨重的 HBase，提供亞毫秒級的推薦清單查詢。
4. **商品微服務 (Product Service)**: 新增一個 `product-service` 來管理真實的商品與機票，補齊電商閉環，不再依賴前端寫死的 Mock Data。

## 🚀 專案現狀

目前系統處於「基礎設施重置」狀態（Clean State）。
所有與 Hadoop/HBase 相關的依賴與程式碼皆已徹底清除。

### 🛍️ 電商交易核心 (CQRS & Event Sourcing)
基於 **Axon Framework** 構建，大量運用 CQRS 與 Event Sourcing 模式處理核心業務。
- **`product-service`**: 商品與設定微服務。負責管理商品型錄、分類設定 (Type/SubType)、使用者最愛清單，並維護商品狀態與可用庫存的查詢視圖 (Read Model)。
- **`order-service`**: 訂單微服務。負責購物車與訂單建立，並實作 **Saga 模式 (`OrderManagementSaga`)** 作為分散式交易的 Orchestrator，協調庫存與付款。
- **`inventory-service`**: 庫存微服務。管理實體庫存水位，處理鎖定庫存 (Reserve)、確認扣減 (Confirm)、取消鎖定 (Cancel) 及庫存歸還等指令。
- **`payment-service`**: 付款微服務。模擬第三方金流串接，處理付款成功、失敗及退款。
- **`shared-apis`**: 跨服務共用的 API 模組 (如 `inventory-api`, `payment-api`)，統一定義 Domain Commands 與 Events。

#### 🔄 OrderManagementSaga 分散式交易流程

以下流程圖展示了 `OrderManagementSaga` 如何協調 `order-service`、`inventory-service` 與 `payment-service`：

```mermaid
stateDiagram-v2
    direction TB
    
    %% 定義各種微服務的色彩標籤
    classDef saga fill:#f9f0ff,stroke:#b19cd9,stroke-width:2px,color:black
    classDef order fill:#e6f3ff,stroke:#4da6ff,stroke-width:2px,color:black
    classDef inventory fill:#e6ffe6,stroke:#4dff4d,stroke-width:2px,color:black
    classDef payment fill:#fff2e6,stroke:#ffa64d,stroke-width:2px,color:black
    classDef error fill:#ffe6e6,stroke:#ff4d4d,stroke-width:2px,color:black

    [*] --> OrderCreatedEvent: 使用者結帳

    state "Saga 啟動 (StartSaga)" as SagaStart {
        OrderCreatedEvent:::order --> ReserveStockCommand
        note right of ReserveStockCommand
            同時設定 10 分鐘 
            Payment Deadline
        end note
    }

    state "庫存預扣階段" as InventoryPhase {
        ReserveStockCommand:::inventory --> StockReservedEvent: 鎖定庫存成功
        ReserveStockCommand:::inventory --> CancelOrderCommand: 鎖定庫存失敗 (商品缺貨)
    }

    state "付款處理階段" as PaymentPhase {
        StockReservedEvent:::inventory --> CreatePaymentCommand
        CreatePaymentCommand:::payment --> PaymentProcessedEvent: 付款成功 (Stripe Webhook)
        
        %% 超時機制
        PaymentDeadline[10分鐘未付款超時]:::error --> CancelOrderCommand
    }

    state "出貨準備階段" as ShipmentPhase {
        PaymentProcessedEvent:::payment --> ConfirmStockReservationCommand
        PaymentProcessedEvent:::payment --> NotifyShipmentCommand
        ConfirmStockReservationCommand:::inventory --> OrderNotifiedEvent
        NotifyShipmentCommand:::order --> OrderNotifiedEvent
    }

    OrderNotifiedEvent:::order --> [*]: Saga 流程結束 (成功)

    %% 補償機制 (Compensation)
    state "Saga 補償機制 (Compensation)" as Compensation {
        CancelOrderCommand:::order --> OrderCancelledEvent
        OrderReturnedEvent:::order --> PerformCompensation
        OrderCancelledEvent:::order --> PerformCompensation
        
        state PerformCompensation {
            direction LR
            state check_payment <<choice>>
            check_payment --> PaymentCompleted: 付款已完成
            check_payment --> PaymentNotCompleted: 付款未完成
            
            PaymentCompleted --> AddStockCommand: 歸還實體庫存
            PaymentCompleted --> RefundPaymentCommand: 發起退款
            
            PaymentNotCompleted --> CancelStockReservationCommand: 釋放庫存鎖定
            PaymentNotCompleted --> CancelPaymentCommand: 取消付款意圖
        }
    }
    
    PerformCompensation --> [*]: Saga 流程終止 (SagaLifecycle.end)
    
    class SagaStart, Compensation saga
```

### 📊 數據與推薦引擎 (Data & Recommendation)
- **`behavior-service`**: 行為採集服務。蒐集前端使用者的瀏覽 (VIEW)、加入最愛 (FAVORITE) 等行為 (準備對接 Kafka)。
- **`recommendation-service`**: 推薦引擎服務。提供個人化推薦清單 (準備對接 Redis)。
- **`spark-recommender`**: 離線推薦模型運算模組。

### 🌐 介面端 (Frontend)
- **`e-commerce-frontend`**: Angular 前端應用程式。涵蓋首頁商品展示、推薦側邊欄、購物車管理與結帳流程。

*(更多啟動與開發指南，將於後續架構補齊後更新於此)*
