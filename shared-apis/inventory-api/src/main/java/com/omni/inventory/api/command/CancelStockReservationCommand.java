package com.omni.inventory.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * CancelStockReservationCommand - 取消/釋放鎖定庫存命令 (付款失敗或超時)
 */
public record CancelStockReservationCommand(@TargetAggregateIdentifier String productId, String orderId, Integer quantity) {
}
