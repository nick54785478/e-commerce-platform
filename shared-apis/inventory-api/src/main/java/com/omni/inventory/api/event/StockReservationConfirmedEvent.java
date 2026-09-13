package com.omni.inventory.api.event;

/**
 * StockReservationConfirmedEvent - 庫存鎖定確認 (扣減) 事件
 */
public record StockReservationConfirmedEvent(String productId, String orderId) {
}
