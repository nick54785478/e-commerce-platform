package com.omni.payment.api.command;

import java.math.BigDecimal;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * CreatePaymentCommand - 建立付款命令
 */
public record CreatePaymentCommand(@TargetAggregateIdentifier String paymentId, String orderId, BigDecimal amount) {
}
