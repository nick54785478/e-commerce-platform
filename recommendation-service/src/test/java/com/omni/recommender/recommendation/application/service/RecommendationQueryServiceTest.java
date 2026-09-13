package com.omni.recommender.recommendation.application.service;

import com.omni.recommender.recommendation.application.port.out.RecommendationCachePort;
import com.omni.recommender.recommendation.application.query.GetRecommendationQuery;
import com.omni.recommender.recommendation.application.view.RecommendationGottenView;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.root.Recommendation;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.RecommendedItem;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.TenantId;
import com.omni.recommender.recommendation.domain.recommendation.aggregate.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RecommendationQueryServiceTest {

    private RecommendationCachePort cachePort;
    private RecommendationQueryService queryService;

    @BeforeEach
    void setUp() {
        cachePort = Mockito.mock(RecommendationCachePort.class);
        queryService = new RecommendationQueryService(cachePort);
    }

    @Test
    void shouldReturnRecommendationWhenFoundInCache() {
        // Arrange
        TenantId tenantId = new TenantId("T1001");
        UserId userId = new UserId("U2001");
        List<RecommendedItem> items = List.of(
                new RecommendedItem("ITEM_A", 0.95, 1),
                new RecommendedItem("ITEM_B", 0.90, 2)
        );
        Recommendation mockRecommendation = Recommendation.restore(tenantId, userId, items, Instant.now());
        when(cachePort.fetchRecommendation(any(TenantId.class), any(UserId.class))).thenReturn(Optional.of(mockRecommendation));

        GetRecommendationQuery query = new GetRecommendationQuery("T1001", "U2001", 5);

        // Act
        RecommendationGottenView view = queryService.execute(query);

        // Assert
        assertEquals("U2001", view.userId());
        assertFalse(view.isFallback());
        assertEquals(2, view.items().size());
        assertEquals("ITEM_A", view.items().get(0).itemId());
    }

    @Test
    void shouldReturnFallbackWhenCacheMisses() {
        // Arrange (模擬 Cache Miss)
        when(cachePort.fetchRecommendation(any(TenantId.class), any(UserId.class))).thenReturn(Optional.empty());
        GetRecommendationQuery query = new GetRecommendationQuery("T9999", "U9999", 2);

        // Act
        RecommendationGottenView view = queryService.execute(query);

        // Assert
        assertEquals("U9999", view.userId());
        assertTrue(view.isFallback()); // 確認啟動了降級策略
        assertEquals(0, view.items().size()); // 預設的 fallback 在這裡設為 empty list
    }
}
