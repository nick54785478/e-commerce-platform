package com.omni.inventory.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * ConfirmStockReservationCommand - 確認扣減庫存命令 (付款成功)
 */
public record ConfirmStockReservationCommand(@TargetAggregateIdentifier String productId, String orderId) {
}
