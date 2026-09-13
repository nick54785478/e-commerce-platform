package com.omni.inventory.api.event;

/**
 * StockReservedEvent - 庫存鎖定成功事件
 */
public record StockReservedEvent(String productId, String orderId, Integer quantity) {
}
