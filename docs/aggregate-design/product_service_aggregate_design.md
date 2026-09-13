# Product Service 聚合設計 (Aggregate Design)

本文件描述 `product-service` 中商品 (`Product`) 的核心領域模型與聚合設計，基於 Domain-Driven Design (DDD) 與 CQRS / Event Sourcing 架構。

## 聚合根：Product

`Product` 是負責管理商品資訊與庫存的核心聚合根 (Aggregate Root)。所有的商品狀態改變（例如：上下架、扣減庫存、更新商品資訊）都必須透過發送 Command 到此聚合根，並由其產生對應的 Event。

### 核心狀態 (Attributes)
- `productId` (String): 聚合根識別碼 (@AggregateIdentifier)
- `tenantId` (String): 租戶識別碼
- `name` (String): 商品名稱
- `description` (String): 商品描述
- `stock` (Integer): 目前可用庫存量
- `imageUrls` (List<String>): 商品圖片連結
- `tags` (List<String>): 商品標籤
- `price` (BigDecimal): 商品價格
- `status` (ProductStatus): 商品狀態 (ACTIVE, INACTIVE 等)
- `version` (Long): 聚合版本號 (@AggregateVersion)，用於樂觀鎖控制

### 支援的業務操作 (Commands / Execution)

| 業務操作 | 對應的方法 | 說明 | 產生的事件 |
|----------|------------|------|------------|
| 建立商品 | `Product.create(...)` | 驗證價格必須大於零，建立商品實體 | `ProductCreatedEvent` |
| 變更狀態 | `changeStatus(...)` | 變更商品的上下架狀態，若狀態無變化則忽略 | `ProductStatusChangedEvent` |
| 扣減庫存 | `reduceStock(...)` | 扣減庫存量。驗證商品是否處於上架狀態，且庫存是否足夠 | `StockReducedEvent` |
| 庫存補償 | `addStock(...)` | 增加（補回）庫存量。通常用於 Saga 交易失敗時的補償機制 | `StockAddedEvent` |
| 更新商品 | `update(...)` | 更新商品基本資訊。驗證價格必須大於零 | `ProductUpdatedEvent` |

### 狀態變更 (Event Sourcing Handlers)

`Product` 聚合根內部的狀態不允許被直接修改，所有屬性的更新皆透過 `@EventSourcingHandler` 監聽由聚合自己發出的事件來改變：

1. **`on(ProductCreatedEvent)`**:
   初始化 `productId`, `tenantId`, `name`, `description`, `price`, `stock`, `imageUrls`, `tags`，並設定 `status` 為 `ACTIVE`。

2. **`on(ProductStatusChangedEvent)`**:
   更新商品的 `status`。

3. **`on(StockReducedEvent)`**:
   將目前的 `stock` 減去事件中指定的數量。

4. **`on(StockAddedEvent)`**:
   將目前的 `stock` 加上事件中指定的數量。

5. **`on(ProductUpdatedEvent)`**:
   更新商品的 `tenantId`, `name`, `description`, `price`, `imageUrls`, `tags`。

## 與其他服務的關聯 (Shared APIs)

- 商品的庫存變更指令 (`ReduceStockCommand`, `AddStockCommand`) 及其對應事件 (`StockReducedEvent`, `StockAddedEvent`) 定義在 `shared-apis/product-api` 模組中。
- 這使得外部服務 (如 `order-service` 中的 `OrderManagementSaga`) 能夠透過發送指令來要求 `product-service` 扣減或增補庫存，而不需要直接依賴 `product-service` 的內部實作。
