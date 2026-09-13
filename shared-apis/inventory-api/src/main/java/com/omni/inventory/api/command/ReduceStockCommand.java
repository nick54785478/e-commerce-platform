package com.omni.inventory.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * ReduceStockCommand - 扣減庫存命令
 * <p>
 * 由 Saga 協調發出，要求 Inventory Aggregate 進行庫存扣減。
 * </p>
 */
public record ReduceStockCommand(@TargetAggregateIdentifier String productId, String orderId, Integer quantity) {
}
