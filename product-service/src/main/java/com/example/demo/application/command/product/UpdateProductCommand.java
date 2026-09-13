package com.example.demo.application.command.product;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateVersion;
import java.math.BigDecimal;
import java.util.List;

/**
 * UpdateProductCommand - 更新商品指令
 */
public record UpdateProductCommand(
        @TargetAggregateIdentifier String productId,
        String tenantId,
        @TargetAggregateVersion Long version,
        String name,
        String description,
        String type,
        String subType,
        BigDecimal price,
        List<String> imageUrls,
        List<String> tags
) {
}
