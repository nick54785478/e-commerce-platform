package com.omni.payment.api.command;

import java.math.BigDecimal;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * ProcessPaymentCommand - 處理付款命令
 */
public record ProcessPaymentCommand(@TargetAggregateIdentifier String paymentId, String orderId, BigDecimal amount) {
}
