package com.example.demo.application.domain.product.query;

/**
 * GetProductQuery - 查詢單一商品詳細資料
 * <p>
 * 用於 CQRS 架構中根據 ID 獲取商品詳細視圖 (View)。
 * </p>
 * 
 * @param tenantId  租戶識別碼
 * @param productId 欲查詢的商品識別碼
 */
public record GetProductQuery(String tenantId, String productId) {
}
