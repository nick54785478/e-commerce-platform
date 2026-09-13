package com.example.demo.application.port.in;

import com.omni.product.api.dto.ProductQueriedView;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Inbound Port (Use Case) - 處理查詢所有商品
 * <p>
 * 遵循 Hexagonal Architecture (六角架構) 中的 Primary Port (Inbound Port)。
 * 負責定義表現層 (如 REST Controller) 可以觸發的應用程式使用案例 (Use Case)，
 * 將業務意圖與底層 Application Service 的實作細節解耦。
 * </p>
 */
public interface FindAllProductsUseCase {
    CompletableFuture<List<ProductQueriedView>> findAll(String tenantId);
}

