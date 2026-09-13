package com.omni.inventory.api.event;

/**
 * StockReservationFailedEvent - 庫存鎖定失敗事件 (庫存不足)
 */
public record StockReservationFailedEvent(String productId, String orderId, String reason) {
}
