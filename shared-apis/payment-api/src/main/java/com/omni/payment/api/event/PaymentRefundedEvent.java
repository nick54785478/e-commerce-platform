package com.omni.payment.api.event;

import java.math.BigDecimal;

/**
 * PaymentRefundedEvent - 付款已退款事件
 */
public record PaymentRefundedEvent(String paymentId, String orderId, BigDecimal amount) {
}
