package com.example.demo.iface.rest;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.application.port.in.AddFavoriteUseCase;
import com.example.demo.application.port.in.RemoveFavoriteUseCase;
import com.example.demo.application.service.FavoriteQueryService;
import com.example.demo.infra.projection.product.entity.ProductView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * UserFavoriteController - 使用者最愛商品 REST API 控制器
 * <p>
 * 提供前端介接新增、移除與查詢使用者最愛商品的端點。
 * 接收 HTTP 請求後，會轉換為 CQRS 的 Command 交由 Axon CommandGateway 發送，
 * 或直接查詢 Read Model (View) 取得資料。
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final AddFavoriteUseCase addFavoriteUseCase;
    private final RemoveFavoriteUseCase removeFavoriteUseCase;
    private final FavoriteQueryService favoriteQueryService;

    /**
     * 新增最愛商品
     *
     * @param productId 商品 ID (路徑參數)
     * @param tenantId  租戶 ID (來自 Header)
     * @param userId    使用者 ID (來自 Header)
     * @return 成功加入後返回最愛紀錄的 ID，若已存在則提示 "Already in favorites"
     */
    @PostMapping("/{productId}")
    public CompletableFuture<ResponseEntity<String>> addFavorite(
            @PathVariable String productId,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default-tenant") String tenantId,
            @RequestHeader(value = "X-User-Id") String userId,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        
        log.info("Request to add favorite: userId={}, productId={}", userId, productId);
        
        return addFavoriteUseCase.addFavorite(tenantId, userId, productId, sessionId)
            .thenApply(ResponseEntity::ok)
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add favorite"));
    }

    /**
     * 移除最愛商品
     *
     * @param productId 商品 ID (路徑參數)
     * @param tenantId  租戶 ID (來自 Header)
     * @param userId    使用者 ID (來自 Header)
     * @return 移除成功的訊息
     */
    @DeleteMapping("/{productId}")
    public CompletableFuture<ResponseEntity<String>> removeFavorite(
            @PathVariable String productId,
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default-tenant") String tenantId,
            @RequestHeader(value = "X-User-Id") String userId) {
        
        log.info("Request to remove favorite: userId={}, productId={}", userId, productId);
        
        return removeFavoriteUseCase.removeFavorite(tenantId, userId, productId)
            .thenApply(v -> ResponseEntity.ok("Removed"))
            .exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to remove favorite"));
    }

    /**
     * 取得使用者的最愛商品列表
     *
     * @param tenantId 租戶 ID (來自 Header)
     * @param userId   使用者 ID (來自 Header)
     * @return 最愛商品的詳細資訊列表 (ProductView)
     */
    @GetMapping
    public ResponseEntity<List<ProductView>> getFavorites(
            @RequestHeader(value = "X-Tenant-Id", defaultValue = "default-tenant") String tenantId,
            @RequestHeader(value = "X-User-Id") String userId) {
        
        log.info("Request to get favorites for userId={}", userId);
        
        List<ProductView> products = favoriteQueryService.getFavorites(tenantId, userId);
        
        return ResponseEntity.ok(products);
    }
}
