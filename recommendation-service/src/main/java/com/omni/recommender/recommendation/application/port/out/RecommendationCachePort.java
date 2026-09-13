package com.omni.recommender.recommendation.application.port.out;

import com.omni.recommender.recommendation.domain.recommendation.aggregate.root.Recommendation;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.root.Recommendation;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.TenantId;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.UserId;

import java.util.Optional;

/**
 * 向底層快取 (Redis) 獲取資料的 Outbound Port
 * 遵循 Hexagonal Architecture 命名規範。
 */
public interface RecommendationCachePort {
    /**
     * 根據 TenantId 與 UserId 獲取推薦清單
     * @param tenantId 租戶識別碼
     * @param userId 用戶識別碼
     * @return 若 Redis 中有 Spark ALS 算好的資料則回傳，否則回傳 empty 以觸發降級策略
     */
    Optional<Recommendation> fetchRecommendation(TenantId tenantId, UserId userId);
}
