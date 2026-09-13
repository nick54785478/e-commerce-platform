package com.example.demo.application.port.in;

import java.util.concurrent.CompletableFuture;

/**
 * Inbound Port (Use Case) - 處理新增最愛商品
 * <p>
 * 遵循 Hexagonal Architecture (六角架構) 中的 Primary Port (Inbound Port)。
 * </p>
 */
public interface AddFavoriteUseCase {
    CompletableFuture<String> addFavorite(String tenantId, String userId, String productId, String sessionId);
}
