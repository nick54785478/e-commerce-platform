package com.example.demo.iface.dto.req;

import com.omni.product.api.enums.ProductStatus;

/**
 * ChangeProductStatusResource - 變更商品狀態的請求 DTO
 */
public record ChangeProductStatusResource(
        Long version,
        ProductStatus status
) {
}
