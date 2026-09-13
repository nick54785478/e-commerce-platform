package com.example.demo.application.domain.favorite.event;

public record FavoriteAddedEvent(
    String favoriteId,
    String tenantId,
    String userId,
    String productId
) {}
