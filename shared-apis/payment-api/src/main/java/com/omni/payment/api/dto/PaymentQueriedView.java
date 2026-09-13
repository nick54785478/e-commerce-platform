package com.omni.payment.api.dto;

import java.math.BigDecimal;

/**
 * PaymentQueriedView - 付款查詢視圖
 */
public record PaymentQueriedView(
		String paymentId,
		String orderId,
		BigDecimal amount,
		String status
) {
}
