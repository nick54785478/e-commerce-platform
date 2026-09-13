package com.omni.product.api.event;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductCreatedEvent - 商品已建立事件
 * <p>
 * 集中定義於 shared-apis，供各個微服務 (如 inventory-service) 監聽。
 * </p>
 */
public record ProductCreatedEvent(
        String productId,
        String tenantId,
        String name,
        String description,
        String type,
        String subType,
        BigDecimal price,
        List<String> imageUrls,
        List<String> tags
) {
}
