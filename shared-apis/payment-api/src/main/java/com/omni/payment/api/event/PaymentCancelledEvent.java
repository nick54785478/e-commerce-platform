package com.omni.payment.api.event;

/**
 * PaymentCancelledEvent - 付款已取消事件
 */
public record PaymentCancelledEvent(String paymentId, String orderId) {
}
