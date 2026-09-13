package com.example.demo.application.command.favorite;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RemoveFavoriteCommand(
    @TargetAggregateIdentifier String favoriteId
) {}
