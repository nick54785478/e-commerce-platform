package com.omni.inventory.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * CreateInventoryItemCommand - 建立庫存項目命令
 */
public record CreateInventoryItemCommand(@TargetAggregateIdentifier String productId, Integer initialStock) {
}
