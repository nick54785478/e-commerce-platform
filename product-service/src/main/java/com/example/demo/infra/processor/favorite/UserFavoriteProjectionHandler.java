package com.example.demo.infra.processor.favorite;

import java.time.LocalDateTime;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.example.demo.application.domain.favorite.event.FavoriteAddedEvent;
import com.example.demo.application.domain.favorite.event.FavoriteRemovedEvent;
import com.example.demo.infra.projection.favorite.repository.UserFavoriteViewRepository;
import com.example.demo.infra.projection.favorite.entity.UserFavoriteView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@ProcessingGroup("favorite-processor")
@RequiredArgsConstructor
public class UserFavoriteProjectionHandler {

    private final UserFavoriteViewRepository repository;

    @EventHandler
    public void on(FavoriteAddedEvent event) {
        log.info("[Favorite Projection] 同步新增最愛商品: {}", event.favoriteId());
        UserFavoriteView view = new UserFavoriteView();
        view.setFavoriteId(event.favoriteId());
        view.setTenantId(event.tenantId());
        view.setUserId(event.userId());
        view.setProductId(event.productId());
        view.setCreatedAt(LocalDateTime.now());
        repository.save(view);
    }

    @EventHandler
    public void on(FavoriteRemovedEvent event) {
        log.info("[Favorite Projection] 同步移除最愛商品: {}", event.favoriteId());
        repository.findById(event.favoriteId()).ifPresent(repository::delete);
    }
}
