package com.omni.recommender.recommendation.infrastructure.adapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.omni.recommender.recommendation.application.port.out.RecommendationCachePort;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.root.Recommendation;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.RecommendedItem;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.TenantId;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 實作 RecommendationCachePort 的 Redis Adapter。
 * 從 Redis 中讀取 Spark ALS 批次任務寫入的推薦結果。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRecommendationCacheAdapter implements RecommendationCachePort {

    private final StringRedisTemplate redisTemplate;
    private final Gson gson;

    @Override
    public Optional<Recommendation> fetchRecommendation(TenantId tenantId, UserId userId) {
        // 使用 SaaS 隔離的 Key 格式
        String key = "tenant:" + tenantId.value() + ":user:" + userId.value() + ":recs";
        
        try {
            String jsonValue = redisTemplate.opsForValue().get(key);
            
            if (jsonValue == null || jsonValue.isEmpty()) {
                log.debug("No recommendation found in Redis for user {}", userId.value());
                return Optional.empty();
            }

            // 解析 Spark 寫入的 JSON 陣列 (List of Maps)
            Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> recsList = gson.fromJson(jsonValue, listType);

            if (recsList == null || recsList.isEmpty()) {
                return Optional.empty();
            }

            // 將解析出來的 Map 轉換為領域層的 RecommendedItem Value Object
            List<RecommendedItem> items = recsList.stream().map(map -> {
                String itemId = (String) map.get("itemId");
                double score = ((Number) map.get("score")).doubleValue();
                int rank = recsList.indexOf(map) + 1; // 根據陣列順序賦予 Rank
                return new RecommendedItem(itemId, score, rank);
            }).collect(Collectors.toList());

            // 封裝為 Recommendation Aggregate Root 回傳
            return Optional.of(Recommendation.restore(tenantId, userId, items, Instant.now()));

        } catch (Exception e) {
            log.error("Failed to fetch recommendation from Redis for tenant {}, user {}", tenantId.value(), userId.value(), e);
            // 發生例外時，回傳 empty 以觸發 Fallback 降級機制
            return Optional.empty();
        }
    }
}
