package com.example.demo.infra.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.omni.product.api.dto.ProductQueriedView;

/**
 * ProductApiClient - 使用 Spring 6 HttpInterface 定義向 product-service 的 HTTP 呼叫
 */
@HttpExchange("/products")
public interface ProductApiClient {

	@GetExchange("/{productId}")
	ProductQueriedView getProduct(@PathVariable("productId") String productId);

}
