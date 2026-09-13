package com.example.demo.application.command.product;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateVersion;
import com.omni.product.api.enums.ProductStatus;

/**
 * ChangeProductStatusCommand - 變更商品狀態指令
 */
public record ChangeProductStatusCommand(
        @TargetAggregateIdentifier String productId,
        String tenantId,
        @TargetAggregateVersion Long version,
        ProductStatus status
) {
}
