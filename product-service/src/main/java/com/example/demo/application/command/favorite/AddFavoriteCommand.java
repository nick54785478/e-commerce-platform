package com.example.demo.application.command.favorite;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AddFavoriteCommand(
    @TargetAggregateIdentifier String favoriteId,
    String tenantId,
    String userId,
    String productId
) {}
