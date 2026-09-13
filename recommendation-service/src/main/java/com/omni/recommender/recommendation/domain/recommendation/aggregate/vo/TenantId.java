package com.omni.recommender.recommendation.domain.recommendation.aggregate.vo;

import java.util.Objects;

/**
 * 租戶 ID (Value Object)
 * 用於 SaaS 架構下隔離不同客戶的資料。
 */
public record TenantId(String value) {
    public TenantId {
        Objects.requireNonNull(value, "Tenant ID cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be empty");
        }
    }
}
