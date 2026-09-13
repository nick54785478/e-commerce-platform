package com.omni.recommender.behavior.domain.behavior.aggregate.vo;

/**
 * 租戶 ID (Value Object)
 * 使用 Java Record 確保不可變性 (Immutability) 並提供內建的 equals/hashCode
 */
public record TenantId(String value) {
    
    public TenantId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be empty");
        }
    }
}
