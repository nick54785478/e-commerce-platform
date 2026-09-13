package com.omni.product.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductQueriedView - 商品查詢視圖 DTO
 */
public record ProductQueriedView(
        String productId,
        String name,
        String description,
        String type,
        String subType,
        BigDecimal price,
        Integer stock,
        Long version,
        List<String> imageUrls,
        List<String> tags,
        com.omni.product.api.enums.ProductStatus status
) {
    public boolean isAvailable() {
        return status == com.omni.product.api.enums.ProductStatus.ACTIVE && stock != null && stock > 0;
    }
}
