package com.omni.payment.api.command;

import java.math.BigDecimal;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * RefundPaymentCommand - 退款付款命令
 */
public record RefundPaymentCommand(@TargetAggregateIdentifier String paymentId, String orderId, BigDecimal amount) {
}
