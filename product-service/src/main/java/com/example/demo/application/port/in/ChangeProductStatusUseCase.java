package com.example.demo.application.port.in;

import com.example.demo.application.command.product.ChangeProductStatusCommand;
import java.util.concurrent.CompletableFuture;

/**
 * Inbound Port (Use Case) - 處理變更商品狀態
 * <p>
 * 遵循 Hexagonal Architecture (六角架構) 中的 Primary Port (Inbound Port)。
 * 負責定義表現層 (如 REST Controller) 可以觸發的應用程式使用案例 (Use Case)，
 * 將業務意圖與底層 Application Service 的實作細節解耦。
 * </p>
 */
public interface ChangeProductStatusUseCase {
    CompletableFuture<Void> changeStatus(ChangeProductStatusCommand command);
}

