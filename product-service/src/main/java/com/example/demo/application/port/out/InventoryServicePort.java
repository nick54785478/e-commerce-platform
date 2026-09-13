package com.example.demo.application.port.out;

import com.omni.inventory.api.command.AdjustStockCommand;

import java.util.concurrent.CompletableFuture;

/**
 * Outbound Port - 庫存服務介面
 * <p>
 * 遵循 Hexagonal Architecture (六角架構) 中的 Secondary Port (Outbound Port)。
 * 負責定義與外部系統（如 Inventory 微服務）互動的合約，
 * 使得 Application Layer 可以呼叫外部服務，但不需要依賴外部服務的具體實作 (如 gRPC, HTTP, 或 Axon Command Gateway)。
 * </p>
 */
public interface InventoryServicePort {

    /**
     * 發送調整庫存的命令至 Inventory 微服務
     *
     * @param command 庫存調整命令，包含欲調整的商品與數量
     * @return 包含執行結果的非同步任務 (CompletableFuture)
     */
    CompletableFuture<Void> adjustStock(AdjustStockCommand command);
}
