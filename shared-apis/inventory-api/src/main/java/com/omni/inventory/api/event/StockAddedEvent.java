package com.omni.inventory.api.event;

/**
 * StockAddedEvent - 庫存已增加/歸還事件
 */
public record StockAddedEvent(String productId, String orderId, Integer quantity) {
}
