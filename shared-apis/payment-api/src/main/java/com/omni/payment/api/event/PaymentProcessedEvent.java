package com.omni.payment.api.event;

import java.math.BigDecimal;

/**
 * PaymentProcessedEvent - 付款已處理事件
 */
public record PaymentProcessedEvent(String paymentId, String orderId, BigDecimal amount) {
}
