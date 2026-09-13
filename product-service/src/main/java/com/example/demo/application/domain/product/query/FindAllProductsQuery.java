package com.example.demo.application.domain.product.query;

/**
 * FindAllProductsQuery - 查詢所有商品指令
 * <p>
 * 用於 CQRS 架構中透過 Query Bus 發送獲取全部商品的請求。
 * </p>
 * 
 * @param tenantId 租戶識別碼
 */
public record FindAllProductsQuery(String tenantId) {
}
