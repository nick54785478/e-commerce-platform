package com.example.demo.application.port.in;

import java.util.concurrent.CompletableFuture;

/**
 * Inbound Port (Use Case) - 處理移除最愛商品
 * <p>
 * 遵循 Hexagonal Architecture (六角架構) 中的 Primary Port (Inbound Port)。
 * </p>
 */
public interface RemoveFavoriteUseCase {
    CompletableFuture<Void> removeFavorite(String tenantId, String userId, String productId);
}
