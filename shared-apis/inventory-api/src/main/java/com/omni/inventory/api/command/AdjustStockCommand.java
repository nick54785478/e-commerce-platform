package com.omni.inventory.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * AdjustStockCommand - 人工調整庫存命令
 * <p>
 * 用於非訂單驅動的後台手動庫存調整 (例如進貨、盤點)。
 * quantity 可正可負。
 * </p>
 */
public record AdjustStockCommand(
        @TargetAggregateIdentifier String productId, 
        Integer quantity, 
        String reason
) {
}
