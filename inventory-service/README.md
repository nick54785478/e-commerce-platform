# Inventory Service (庫存微服務)

這是一個基於 **Axon Framework** 實作，專門負責電商平台「庫存鎖定與扣減」的微服務。
本專案採用 **事件溯源 (Event Sourcing)** 模式構建，透過高度內聚的領域驅動設計 (DDD)，確保在高併發環境下庫存資料的絕對正確性，杜絕「超賣」現象。

**技術棧與基礎設施**

| 分類 | 技術選型 | 職責說明 |
| --- | --- | --- |
| 核心框架 | Axon Framework | 提供 Command/Event Bus 與 Event Sourcing 支援 |
| 持久化 (寫) | Axon Server | 專用的 Event Store，存儲不可變的庫存變更事實 |
| API 定義 | shared-apis (`inventory-api`) | 跨服務共用的 Domain Commands 與 Events |

---

## 核心架構：領域模型 (Domain Aggregate)

專案內僅包含單一核心聚合根：

* **`InventoryItem` (庫存項目)**：
  專注於管理特定商品的庫存水位 (`stock`) 以及被訂單預留的庫存 (`reservations`)。
  本聚合根不處理商品型錄等其餘資訊，徹底實踐微服務與 DDD 邊界劃分。

> **⚠️ Axon Aggregate ID 碰撞處理機制：**
> 為了避免在同一個 Axon Server 中，`InventoryItem` 的 Aggregate Identifier 與 `product-service` 的 `Product` 發生 ID 碰撞，
> 本系統在呼叫庫存指令時 (例如從 `OrderManagementSaga`)，會自動將 `productId` 加上 **`INV-`** 前綴，以區隔不同聚合根的事件流。

---

## 庫存鎖定與扣減流程 (Two-Phase Commit 概念)

為了配合 `order-service` 的 Saga 分散式交易，本服務實作了類似兩階段提交 (2PC) 的庫存鎖定機制：

1. **預留庫存 (Reserve Stock)**
   * **指令**: `ReserveStockCommand`
   * **情境**: 使用者點擊「結帳」建立訂單時。
   * **邏輯**: 驗證 `目前總庫存 - 已鎖定庫存 >= 請求數量`。若驗證通過，發佈 `StockReservedEvent` 並將該數量記錄於記憶體的 `reservations` Map 中。這能保證該庫存不會被其他人買走，同時也立刻透過 Event Bus 通知 `product-service` 扣減畫面的「可用庫存」。

2. **確認扣減 (Confirm Reservation)**
   * **指令**: `ConfirmStockReservationCommand`
   * **情境**: 訂單成功完成付款時。
   * **邏輯**: 找到對應的 `orderId` 預留紀錄，將預留數量正式從 `stock` (總庫存) 中扣除，並清除 `reservations` 紀錄，發佈 `StockReservationConfirmedEvent`。

3. **釋放鎖定 (Cancel Reservation)**
   * **指令**: `CancelStockReservationCommand`
   * **情境**: 訂單未付款超時，或付款失敗時。
   * **邏輯**: 找到對應的預留紀錄並將其丟棄，發佈 `StockReservationCancelledEvent`，這會通知 `product-service` 將畫面的「可用庫存」補回。

---

## 其他庫存維護指令

除了一般的購物流程外，本服務亦支援以下基礎操作：

* **`AddStockCommand` (進貨 / 歸還庫存)**：直接增加實體總庫存 (例如：訂單退貨、實體進貨)。
* **`ReduceStockCommand` (直接扣減庫存)**：不經過預留，強制扣除庫存。
* **`AdjustStockCommand` (手動校正庫存)**：管理員手動調整盤點庫存落差，發佈 `StockAdjustedEvent`。

## 快速開始

1. 確保 Axon Server 正常運行 (可透過根目錄的 docker-compose 建立容器)。
2. 本微服務無須外部的關聯式資料庫 (其狀態由 Axon Server 中的 Event Store 重建)。
3. 確保 `shared-apis` 已經成功 `mvn install` 編譯。
4. 啟動 Spring Boot 應用程式即可開始服務。
