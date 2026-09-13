package com.example.demo.application.domain.order.aggregate.vo;

import java.math.BigDecimal;

/**
 * OrderItem - 訂單明細
 * <p>代表訂單中的單個商品與其對應的數量與價格。</p>
 */
public record OrderItem(
    String productId,
    Integer quantity,
    BigDecimal price
) {
    /**
     * 計算此明細的小計
     */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
