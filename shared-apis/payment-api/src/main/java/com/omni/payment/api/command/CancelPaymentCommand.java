package com.omni.payment.api.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * CancelPaymentCommand - 取消付款命令
 */
public record CancelPaymentCommand(@TargetAggregateIdentifier String paymentId, String orderId) {
}
