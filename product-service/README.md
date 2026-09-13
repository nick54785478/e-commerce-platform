# Product Service (商品與設定微服務)

這是一個基於 Axon Framework 實作的高性能商品管理微服務。本專案採用事件溯源 (Event Sourcing) 與 CQRS (命令查詢職責分離) 模式構建，主要負責管理電商平台中的商品型錄、系統設定 (如分類 Type/SubType) 以及使用者的最愛清單，同時也擔任前端商品瀏覽與搜尋的核心查詢端點。

**技術棧與基礎設施**

| 分類 | 技術選型 | 職責說明 |
| --- | --- | --- |
| 核心框架 | Axon Framework | 提供 Command/Event/Query Bus 與生命週期管理 | 
| 持久化 (寫) | Axon Server | 專用的 Event Store，存儲不可變的事實 (Facts) | 
| 持久化 (讀) | MySQL + JPA | 針對查詢優化的投影模型 (Read Model) | 
| 資料轉換 | MapStruct | 實作 ACL (防腐層)，確保 Domain 與 API 契約解耦 | 

---

## 核心架構：領域模型 (Domain Aggregates)

專案遵循整潔架構原則，確保業務邏輯 (Domain Layer) 與技術細節（Infrastructure Layer）完全隔離。

* **Product Aggregate (商品)**：負責商品資料維護，發佈建立、更新與上下架狀態變更事件。
* **Setting Aggregate (設定)**：負責全站設定值維護，特別是用於動態管理商品分類 (Type) 與次分類 (SubType) 的樹狀結構。
* **UserFavorite Aggregate (最愛)**：管理使用者的最愛商品清單。

## CQRS 與讀寫分離設計

`product-service` 是電商平台的「門面」，承載了極大量的查詢請求，因此在架構上徹底落實了 CQRS 模式：

* **命令端 (Command Side)**：
  透過 `ProductCommandService` 等元件發送指令至 Axon Command Bus，交由各個 Aggregate 進行商業邏輯驗證並發佈事件。
* **查詢端 (Query Side / Read Model)**：
  透過 `ProductProjectionHandler` 監聽 Event Bus。當接收到事件時，即時更新關聯式資料庫 (MySQL) 內的 View Table (如 `product_view`)，並支援高效能的模糊搜尋與分頁。

## 跨服務資料同步與庫存處理

雖然實體庫存由獨立的 `inventory-service` 負責管理與驗證，但為了讓前端能在瀏覽商品時即時看到正確的「可用庫存」，本服務實作了跨服務的事件監聽：

* **即時反映可用庫存**：
  `ProductProjectionHandler` 會監聽來自 `inventory-service` 的 `StockReservedEvent` (庫存鎖定) 與 `StockReservationCancelledEvent` (鎖定取消) 等事件，動態增減 `product_view` 資料表中的 `stock` 欄位。
* **隔離性**：
  商品微服務僅更新「讀取視圖」(Read Model) 以滿足展示需求，不干涉真實的庫存交易與超賣驗證。

---

## 技術關鍵點 (Technical Highlights)

* **Event Sourcing (事件溯源)**： 所有領域狀態變更均由事件驅動，提供完美的審計日誌與歷史追溯。
* **Snapshotting (快照機制)**： 配置快照觸發機制，確保長生命週期的 Aggregate (如 Setting) 在重建狀態時效能恆定在 $O(1)$。
* **樂觀鎖控制 (Optimistic Locking)**： 聚合根使用 `@AggregateVersion`。前端更新資料時必須攜帶 version，由 Axon 自動偵測併發衝突，杜絕 Lost Update。
* **非同步 API**： 全面採用 `CompletableFuture<ResponseEntity<T>>` 處理 Controller 回應，釋放 Servlet Thread，大幅提升系統吞吐量。

## 快速開始

1. 啟動 Axon Server、MySQL (可透過根目錄的 docker-compose 建立容器)。
2. 配置 MySQL `product_view`、`setting_view` 與 `user_favorite_view` 等表結構。
3. 啟動 Spring Boot 應用程式即可開始服務。
