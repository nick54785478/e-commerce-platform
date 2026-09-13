package com.omni.inventory.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * AddStockCommand - 增加/歸還庫存命令
 * <p>
 * 訂單失敗或取消時，Saga 發出此命令以補償庫存。
 * </p>
 */
public record AddStockCommand(@TargetAggregateIdentifier String productId, String orderId, Integer quantity) {
}
