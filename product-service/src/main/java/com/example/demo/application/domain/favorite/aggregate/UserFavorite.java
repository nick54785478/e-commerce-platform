package com.example.demo.application.domain.favorite.aggregate;

import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.example.demo.application.domain.favorite.event.FavoriteAddedEvent;
import com.example.demo.application.domain.favorite.event.FavoriteRemovedEvent;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UserFavorite Aggregate Root - 使用者最愛商品聚合根
 * <p>
 * 屬於 Domain Layer (領域層)，負責封裝最愛商品的核心狀態與業務邏輯。
 * 每個實體代表一位使用者對一項商品的最愛紀錄。
 * </p>
 */
@Slf4j
@Aggregate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFavorite {

    /**
     * 聚合根的唯一識別碼 (Aggregate Identifier)
     */
    @AggregateIdentifier
    private String favoriteId;
    
    /**
     * 租戶 ID
     */
    private String tenantId;

    /**
     * 使用者 ID
     */
    private String userId;

    /**
     * 商品 ID
     */
    private String productId;

    /**
     * 狀態旗標：標示該最愛紀錄是否有效（未被移除）
     */
    private boolean active;

    /**
     * 工廠方法 - 新增最愛商品
     * <p>
     * 當收到新增最愛指令時由 Handler 呼叫，會發佈 {@link FavoriteAddedEvent}，
     * 藉由事件溯源機制初始化此聚合根的狀態。
     * </p>
     */
    public static UserFavorite create(String favoriteId, String tenantId, String userId, String productId) {
        log.info("[Favorite Domain] 添加最愛商品: favoriteId={}, userId={}, productId={}", 
                 favoriteId, userId, productId);
        
        UserFavorite favorite = new UserFavorite();
        AggregateLifecycle.apply(new FavoriteAddedEvent(
            favoriteId, 
            tenantId, 
            userId, 
            productId
        ));
        return favorite;
    }

    /**
     * 業務邏輯 - 移除最愛商品
     * <p>
     * 當收到移除最愛指令時呼叫，會檢查當前狀態。
     * 若已為非活躍狀態 (active = false) 則略過；
     * 若為活躍狀態，則發佈 {@link FavoriteRemovedEvent}。
     * </p>
     */
    public void remove(String favoriteId) {
        if (!this.active) {
            log.warn("[Favorite Domain] 嘗試移除不存在或已移除的最愛商品: favoriteId={}", favoriteId);
            return;
        }
        log.info("[Favorite Domain] 移除最愛商品: favoriteId={}", favoriteId);
        AggregateLifecycle.apply(new FavoriteRemovedEvent(favoriteId));
    }

    /**
     * 事件溯源處理器 (Event Sourcing Handler) - 處理新增事件
     * <p>
     * 此方法將 {@link FavoriteAddedEvent} 的資料同步到 Aggregate 本身的狀態中。
     * 同時將狀態設為活躍 (active = true)。
     * </p>
     *
     * @param event 新增最愛商品的事件
     */
    @EventSourcingHandler
    protected void on(FavoriteAddedEvent event) {
        this.favoriteId = event.favoriteId();
        this.tenantId = event.tenantId();
        this.userId = event.userId();
        this.productId = event.productId();
        this.active = true;
    }

    /**
     * 事件溯源處理器 (Event Sourcing Handler) - 處理移除事件
     * <p>
     * 此方法將 Aggregate 的狀態標記為非活躍 (active = false)。
     * </p>
     *
     * @param event 移除最愛商品的事件
     */
    @EventSourcingHandler
    protected void on(FavoriteRemovedEvent event) {
        this.active = false;
    }
}
