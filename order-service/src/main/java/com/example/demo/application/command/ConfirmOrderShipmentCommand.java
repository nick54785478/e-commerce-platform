package com.example.demo.application.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateVersion;

/**
 * ConfirmOrderShipmentCommand - 客服確認出貨指令
 */
public record ConfirmOrderShipmentCommand(@TargetAggregateIdentifier String orderId,
		@TargetAggregateVersion Long version) {
}
