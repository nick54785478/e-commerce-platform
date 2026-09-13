package com.example.demo.application.service;

import com.example.demo.infra.projection.product.repository.ProductViewRepository;
import com.example.demo.infra.projection.favorite.repository.UserFavoriteViewRepository;
import com.example.demo.infra.projection.favorite.entity.UserFavoriteView;
import com.example.demo.infra.projection.product.entity.ProductView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for querying user favorites.
 * Encapsulates the read model logic and transaction boundary.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteQueryService {

    private final UserFavoriteViewRepository favoriteRepository;
    private final ProductViewRepository productRepository;

    /**
     * Get user's favorite products.
     * Transaction boundary ensures lazy loaded collections can be initialized properly.
     */
    @Transactional(readOnly = true)
    public List<ProductView> getFavorites(String tenantId, String userId) {
        log.debug("Querying favorites for userId={}", userId);
        
        // 從 Read Model 中找出該使用者所有的最愛紀錄
        List<UserFavoriteView> favoriteViews = favoriteRepository.findByTenantIdAndUserId(tenantId, userId);
        List<String> productIds = favoriteViews.stream().map(UserFavoriteView::getProductId).collect(Collectors.toList());
        
        // 根據取得的商品 ID 列表，查詢商品的詳細視圖資料
        List<ProductView> products = productRepository.findAllById(productIds);
        
        // 強制初始化延遲加載的集合，避免 Jackson 序列化時發生 LazyInitializationException (因為 open-in-view=false)
        if (products != null) {
            products.forEach(p -> {
                if (p.getImageUrls() != null) p.getImageUrls().size();
                if (p.getTags() != null) p.getTags().size();
            });
        }
        
        return products;
    }
}
