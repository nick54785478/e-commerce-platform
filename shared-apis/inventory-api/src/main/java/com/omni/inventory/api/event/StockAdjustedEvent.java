package com.omni.inventory.api.event;

/**
 * StockAdjustedEvent - 人工庫存調整事件
 */
public record StockAdjustedEvent(
        String productId, 
        Integer quantity, 
        String reason
) {
}
