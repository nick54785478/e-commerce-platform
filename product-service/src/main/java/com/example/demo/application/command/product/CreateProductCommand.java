package com.example.demo.application.command.product;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import java.math.BigDecimal;
import java.util.List;

/**
 * CreateProductCommand - 建立商品指令
 */
public record CreateProductCommand(
        @TargetAggregateIdentifier
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
