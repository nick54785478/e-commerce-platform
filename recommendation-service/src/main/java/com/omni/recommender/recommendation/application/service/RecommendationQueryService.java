package com.omni.recommender.recommendation.application.service;

import com.omni.recommender.recommendation.application.port.in.GetRecommendationUseCase;
import com.omni.recommender.recommendation.application.port.out.RecommendationCachePort;
import com.omni.recommender.recommendation.application.query.GetRecommendationQuery;
import com.omni.recommender.recommendation.application.view.RecommendationGottenView;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.root.Recommendation;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.RecommendedItem;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.TenantId;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.UserId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 負責查詢推薦清單的 Application Service (Query Service)
 */
@Service
class RecommendationQueryService implements GetRecommendationUseCase {

    private final RecommendationCachePort recommendationCachePort;

    // 定義降級策略使用的預設熱門商品清單 (Fallback)
    private static final List<RecommendedItem> DEFAULT_POPULAR_ITEMS = List.of();

    public RecommendationQueryService(RecommendationCachePort recommendationCachePort) {
        this.recommendationCachePort = recommendationCachePort;
    }

    @Override
    public RecommendationGottenView execute(GetRecommendationQuery query) {
        TenantId tenantId = new TenantId(query.tenantId());
        UserId userId = new UserId(query.userId());
        Recommendation finalRecommendation;

        // 1. 優先透過 Cache Port 取得 Spark ALS 離線運算好的推薦清單 (來自 Redis)
        Optional<Recommendation> userRecommendation = recommendationCachePort.fetchRecommendation(tenantId, userId);

        if (userRecommendation.isPresent()) {
            finalRecommendation = userRecommendation.get();
        } else {
            // 2. 若 Redis 查無資料 (如冷啟動的新使用者)，則使用預設熱門商品做降級 (Fallback)
            finalRecommendation = Recommendation.fallback(tenantId, userId, DEFAULT_POPULAR_ITEMS);
        }

        List<RecommendationGottenView.ItemView> itemViews = finalRecommendation.getItems().stream()
                .limit(query.limit())
                .map(item -> new RecommendationGottenView.ItemView(item.itemId(), item.score(), item.rank()))
                .collect(Collectors.toList());

        return new RecommendationGottenView(
                finalRecommendation.getUserId().value(),
                itemViews,
                finalRecommendation.getGeneratedAt(),
                finalRecommendation.isFallback()
        );
    }
}
