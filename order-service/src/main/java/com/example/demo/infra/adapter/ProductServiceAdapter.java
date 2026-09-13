package com.example.demo.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.demo.application.port.out.ProductServicePort;
import com.example.demo.infra.client.ProductApiClient;
import com.omni.product.api.dto.ProductQueriedView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProductServiceAdapter - 實作 ProductServicePort，並透過 HttpInterface 客戶端呼叫遠端服務
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceAdapter implements ProductServicePort {

	private final ProductApiClient productApiClient;

	@Override
	public ProductQueriedView getProduct(String productId) {
		try {
			log.info("[ProductAdapter] 正在透過 HttpInterface 查詢商品: {}", productId);
			return productApiClient.getProduct(productId);
		} catch (Exception e) {
			log.error("[ProductAdapter] 查詢商品失敗: {}, 原因: {}", productId, e.getMessage());
			// 根據業務邏輯，這裡可以拋出自定義異常，或回傳 null 交由上層處理
			return null;
		}
	}
}
