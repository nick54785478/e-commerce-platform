# Payment Aggregate 設計文件

## 1. 概述 (Overview)
`Payment` 是 `payment-service` 中的核心聚合根 (Aggregate Root)，負責處理與金流相關的業務邏輯，包含付款意圖的建立、扣款，以及後續的取消與退款流程。此聚合根採用了 Event Sourcing (事件溯源) 模式與 CQRS 架構進行設計。

## 2. 狀態屬性 (State)
| 屬性名稱 | 型別 | 說明 |
|---|---|---|
| `paymentId` | `String` | 聚合根唯一識別碼 (`@AggregateIdentifier`) |
| `version` | `Long` | 樂觀鎖版本控制 (`@AggregateVersion`)，防止併發更新導致的資料遺失 |
| `status` | `String` | 支付狀態 (包含：`CREATED`, `PROCESSED`, `CANCELLED`, `REFUNDED`) |
| `amount` | `BigDecimal` | 付款金額 |

## 3. 狀態流轉 (State Machine)
- `(初始化)` ➔ `CREATED` (建立付款紀錄)
- `CREATED` ➔ `PROCESSED` (成功完成扣款)
- `CREATED` ➔ `CANCELLED` (尚未扣款即被取消)
- `PROCESSED` ➔ `REFUNDED` (已扣款後被退款)

## 4. 命令處理與業務規則 (Command Handlers & Business Rules)

### 4.1 建立付款 (`CreatePaymentCommand`)
- **說明**：接收 Saga 或前端發起的付款建立請求。
- **業務規則**：無特定前置條件。
- **產生事件**：`PaymentCreatedEvent`

### 4.2 處理扣款 (`ProcessPaymentCommand`)
- **說明**：實際執行付款扣款動作。
- **業務規則**：
  1. **狀態檢查**：當前狀態必須是 `CREATED`。
  2. **金額一致性校驗**：傳入的扣款金額必須跟建立時的金額 (`this.amount`) 相同。
  3. **授權檢查 (保護機制)**：模擬大額交易阻擋，若金額超過 `100,000` 則判斷為餘額不足或交易受限，拋出 `InsufficientFundsException` 拒絕交易。
- **產生事件**：`PaymentProcessedEvent` (若檢查皆通過)

### 4.3 取消付款 (`CancelPaymentCommand`)
- **說明**：取消尚未完成扣款的付款 (通常由 Saga 訂單超時觸發，或使用者主動取消)。
- **業務規則**：
  1. **冪等性控制**：如果狀態已經是 `CANCELLED` 或 `REFUNDED`，則直接忽略 (不拋例外，確保冪等性)。
  2. **狀態檢查**：如果狀態已經是 `PROCESSED` (已扣款)，則不允許直接取消，需走退款流程，直接忽略或阻擋取消。
- **產生事件**：`PaymentCancelledEvent`

### 4.4 處理退款 (`RefundPaymentCommand`)
- **說明**：對已經完成扣款的付款進行退款操作。
- **業務規則**：
  1. **冪等性控制**：如果狀態已經是 `REFUNDED`，則直接忽略避免重複退款。
  2. **狀態檢查**：當前狀態必須是已扣款 `PROCESSED`，否則拋出例外拒絕退款。
- **產生事件**：`PaymentRefundedEvent`

## 5. 事件溯源處理 (Event Sourcing Handlers)
這些處理器負責將發佈的領域事件還原回聚合的當前狀態：
- **`on(PaymentCreatedEvent)`**：設定 `paymentId`、`amount`，並將 `status` 設為 `CREATED`。
- **`on(PaymentProcessedEvent)`**：將 `status` 變更為 `PROCESSED`。
- **`on(PaymentCancelledEvent)`**：將 `status` 變更為 `CANCELLED`。
- **`on(PaymentRefundedEvent)`**：將 `status` 變更為 `REFUNDED`。
