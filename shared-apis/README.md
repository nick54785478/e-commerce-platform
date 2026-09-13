# Shared APIs (共用模組)

本模組為電商平台 (Omni Recommender Platform) 的共用 API 層，主要定義了各個微服務之間進行溝通時所需要的共通合約 (Contracts)。
它透過分離介面與實作，確保各個微服務（如 `order-service`, `product-service`, `behavior-service` 等）之間的鬆耦合。

## 模組作用與功能

`shared-apis` 作為跨服務通訊的核心樞紐，提供以下主要功能：

1. **定義共用指令 (Commands)**：包含跨系統的 Saga 交易所需的指令（例如 `ReduceStockCommand`, `AddStockCommand` 等），讓不同的服務可以使用相同的指令模型。
2. **定義共用事件 (Events)**：提供跨服務所需的領域事件（例如 `StockReducedEvent`, `StockAddedEvent` 等），確保不同模組對於事件結構的認知一致，避免反序列化錯誤。
3. **共用列舉與資料結構 (Enums & DTOs)**：定義了跨服務會用到的共用狀態（如 `ProductStatus`, `OrderStatus`）與傳輸物件。

## 子模組結構

- **`common-api`**: 最基礎的共用元件，包含跨服務通用的基礎架構設定、共用的 Exceptions 或基礎型別。
- **`product-api`**: 商品服務 (`product-service`) 的公開合約，包含庫存扣減、增補指令及相關事件。
- **`order-api`**: 訂單服務 (`order-service`) 的公開合約，包含訂單狀態變更或跨領域 Saga 會呼叫到的相關指令。

## 為什麼需要 Shared APIs?

在 Event-Driven Architecture 與 CQRS 架構下，不同微服務之間的通訊高度依賴非同步事件與指令 (Axon Framework)。
將這些 Event 與 Command 獨立抽出到 `shared-apis` 模組中，有以下好處：
- **減少重複代碼 (DRY)**：不需要在每個服務中重複定義相同的 Event 類別。
- **避免強耦合**：微服務只需依賴 `shared-api` 模組，不需要直接依賴其他微服務的程式碼庫。
- **確保反序列化相容**：在 Event Sourcing 中，歷史事件需要被準確地反序列化，透過統一的 API 模組管理，可避免 ClassNotFound 或是結構不符的問題。
