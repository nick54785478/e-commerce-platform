package com.example.demo.application.domain.product.event;

import com.omni.product.api.enums.ProductStatus;

/**
 * ProductStatusChangedEvent - 商品狀態變更事件
 */
public record ProductStatusChangedEvent(
        String productId,
        String tenantId,
        ProductStatus status
) {
}
