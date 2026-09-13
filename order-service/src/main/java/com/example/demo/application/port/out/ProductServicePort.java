package com.example.demo.application.port.out;

import com.omni.product.api.dto.ProductQueriedView;

/**
 * ProductServicePort - 定義向外部商品服務取得資訊的介面
 */
public interface ProductServicePort {
	ProductQueriedView getProduct(String productId);
}
