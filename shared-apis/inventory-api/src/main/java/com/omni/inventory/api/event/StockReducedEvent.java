package com.omni.inventory.api.event;

/**
 * StockReducedEvent - 庫存已扣減事件
 * <p>
 * 代表庫存已成功扣減。
 * </p>
 */
public record StockReducedEvent(String productId, String orderId, Integer quantity) {
}
