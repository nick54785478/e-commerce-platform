package com.omni.payment.api.event;

import java.math.BigDecimal;

/**
 * PaymentCreatedEvent - 付款已建立事件
 */
public record PaymentCreatedEvent(String paymentId, String orderId, BigDecimal amount) {
}
