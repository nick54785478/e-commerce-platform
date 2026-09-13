package com.example.demo.infra.processor.favorite;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.Repository;
import org.springframework.stereotype.Component;

import com.example.demo.application.command.favorite.AddFavoriteCommand;
import com.example.demo.application.command.favorite.RemoveFavoriteCommand;
import com.example.demo.application.domain.favorite.aggregate.UserFavorite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UserFavorite Command Handler
 * <p>
 * 遵循 Hexagonal Architecture，將 @CommandHandler 從 Aggregate 中抽出，
 * 由此組件負責注入 Repository，並將 Command 委派給 Aggregate 的業務邏輯方法。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserFavoriteCommandHandler {

    private final Repository<UserFavorite> userFavoriteRepository;

    /**
     * 處理新增最愛商品的指令
     *
     * @param command 新增最愛商品的指令
     */
    @CommandHandler
    public void handle(AddFavoriteCommand command) throws Exception {
        log.info("[Favorite CommandHandler] 處理新增最愛商品指令: favoriteId={}", command.favoriteId());
        userFavoriteRepository.newInstance(() -> UserFavorite.create(
                command.favoriteId(),
                command.tenantId(),
                command.userId(),
                command.productId()
        ));
    }

    /**
     * 處理移除最愛商品的指令
     *
     * @param command 移除最愛商品的指令
     */
    @CommandHandler
    public void handle(RemoveFavoriteCommand command) {
        log.info("[Favorite CommandHandler] 處理移除最愛商品指令: favoriteId={}", command.favoriteId());
        userFavoriteRepository.load(command.favoriteId()).execute(favorite -> {
            favorite.remove(command.favoriteId());
        });
    }
}
