package com.omni.inventory.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * ReserveStockCommand - 鎖定庫存命令
 */
public record ReserveStockCommand(@TargetAggregateIdentifier String productId, String orderId, Integer quantity) {
}
