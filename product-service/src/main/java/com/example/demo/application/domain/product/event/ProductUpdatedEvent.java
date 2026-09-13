package com.example.demo.application.domain.product.event;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductUpdatedEvent - 商品已更新事件
 */
public record ProductUpdatedEvent(
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
