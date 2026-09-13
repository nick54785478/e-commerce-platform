package com.omni.inventory.api.event;

/**
 * StockReservationCancelledEvent - 庫存鎖定取消 (釋放) 事件
 */
public record StockReservationCancelledEvent(String productId, String orderId, Integer quantity) {
}
