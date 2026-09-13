package com.omni.order.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * OrderQueriedView - 訂單查詢視圖
 */
public record OrderQueriedView(
		String orderId,
		BigDecimal amount,
		String status,
		List<OrderItemQueriedView> items
) {
	/**
	 * OrderItemQueriedView - 訂單項目查詢視圖 (Record)
	 * 
	 * @param productId 商品唯一識別碼
	 * @param quantity  購買數量
	 * @param price     購買時單價
	 */
	public record OrderItemQueriedView(String productId, Integer quantity, BigDecimal price) {
	}
}
