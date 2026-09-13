package com.example.demo.application.domain.order.event;

/**
 * OrderShippedEvent - 表示訂單已確認出貨
 */
public record OrderShippedEvent(String orderId) {
}
