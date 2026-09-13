package com.example.demo.application.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import com.example.demo.application.command.favorite.AddFavoriteCommand;
import com.example.demo.application.command.favorite.RemoveFavoriteCommand;
import com.example.demo.application.port.in.AddFavoriteUseCase;
import com.example.demo.application.port.in.RemoveFavoriteUseCase;
import com.example.demo.application.port.out.BehaviorServicePort;
import com.example.demo.infra.projection.favorite.repository.UserFavoriteViewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UserFavorite Command Service - 使用者最愛商品命令應用服務
 * <p>
 * 負責實作 AddFavoriteUseCase 與 RemoveFavoriteUseCase。
 * 承接表現層傳來的請求，包含檢查重複加入等商業邏輯，並透過 Axon CommandGateway 發送 Command。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFavoriteCommandService implements AddFavoriteUseCase, RemoveFavoriteUseCase {

    private final CommandGateway commandGateway;
    private final UserFavoriteViewRepository favoriteRepository;
    private final BehaviorServicePort behaviorService;

    @Override
    public CompletableFuture<String> addFavorite(String tenantId, String userId, String productId, String sessionId) {
        log.info("[Favorite CommandService] 處理新增最愛: userId={}, productId={}", userId, productId);
        
        // 檢查是否已加入最愛，避免重複新增
        if (favoriteRepository.findByTenantIdAndUserIdAndProductId(tenantId, userId, productId).isPresent()) {
            return CompletableFuture.completedFuture("Already in favorites");
        }
        
        String favoriteId = UUID.randomUUID().toString();
        
        return commandGateway.send(new AddFavoriteCommand(favoriteId, tenantId, userId, productId))
            .thenApply(result -> {
                // Log behavior event
                behaviorService.logBehavior(userId, sessionId, productId, "FAVORITE", tenantId);
                return favoriteId;
            })
            .toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> removeFavorite(String tenantId, String userId, String productId) {
        log.info("[Favorite CommandService] 處理移除最愛: userId={}, productId={}", userId, productId);
        
        // 查詢該使用者的最愛紀錄，若存在則發送移除指令
        return favoriteRepository.findByTenantIdAndUserIdAndProductId(tenantId, userId, productId)
            .map(view -> commandGateway.send(new RemoveFavoriteCommand(view.getFavoriteId())).thenApply(r -> (Void) null).toCompletableFuture())
            .orElse(CompletableFuture.completedFuture(null));
    }
}
