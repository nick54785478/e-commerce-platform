package com.example.demo.application.service;

import com.example.demo.application.command.product.ChangeProductStatusCommand;
import com.example.demo.application.command.product.CreateProductCommand;
import com.example.demo.application.command.product.UpdateProductCommand;
import com.example.demo.application.port.out.InventoryServicePort;
import com.omni.inventory.api.command.AdjustStockCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.command.Repository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import com.example.demo.application.port.in.CreateProductUseCase;
import com.example.demo.application.port.in.UpdateProductUseCase;
import com.example.demo.application.port.in.ChangeProductStatusUseCase;
import com.example.demo.application.port.in.AdjustStockUseCase;

/**
 * Product Command Service - 商品命令應用服務 (處理 Command)
 *
 * <p>
 * 屬於 Clean Architecture / CQRS 架構中的 Application Layer (負責 Inbound Service)。
 * 直接承接表現層 (Presentation Layer / API) 的 Command，
 * 並透過 Axon 的 {@link Repository} 讀取或建立 Domain Layer 的 Aggregate，委派業務邏輯。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCommandService implements CreateProductUseCase, UpdateProductUseCase, ChangeProductStatusUseCase, AdjustStockUseCase {

    private final CommandGateway commandGateway;
    private final InventoryServicePort inventoryService;

    /**
     * 處理建立商品
     * <p>
     * 接收表現層傳入的建立商品命令，並將其派發至 Command Gateway 進行非同步的 CQRS 處理。
     * 成功發布後會回傳該任務的 CompletableFuture。
     * </p>
     *
     * @param command 建立商品命令，包含商品初始資訊
     * @return 包含商品 ID 的非同步任務 (CompletableFuture)
     * @throws Exception 若派發命令過程發生錯誤
     */
    @Override
    public CompletableFuture<String> create(CreateProductCommand command) throws Exception {
        log.info("[Product Service] 接收建立商品命令: {}", command.name());
        return commandGateway.send(command);
    }

    /**
     * 處理更新商品
     * <p>
     * 透過 Command Gateway 非同步派發更新命令。
     * </p>
     *
     * @param command 更新商品命令，包含要變更的商品資訊與樂觀鎖版本號 (version)
     * @return 非同步任務 (CompletableFuture)
     */
    @Override
    public CompletableFuture<Void> update(UpdateProductCommand command) {
        log.info("[Product Service] 接收更新商品命令: {}", command.productId());
        return commandGateway.send(command);
    }

    /**
     * 處理變更商品狀態 (上下架)
     * <p>
     * 透過 Command Gateway 非同步派發變更狀態命令。
     * </p>
     *
     * @param command 變更商品狀態命令
     * @return 非同步任務 (CompletableFuture)
     */
    @Override
    public CompletableFuture<Void> changeStatus(ChangeProductStatusCommand command) {
        log.info("[Product Service] 接收變更商品狀態: {}, 新狀態: {}", command.productId(), command.status());
        return commandGateway.send(command);
    }

    /**
     * 調整庫存
     * <p>
     * 透過 Outbound Port (InventoryServicePort) 呼叫遠端 Inventory Service 進行庫存增減作業。
     * </p>
     *
     * @param command 調整庫存命令，包含商品 ID 與異動數量
     * @return 非同步任務 (CompletableFuture)
     */
    @Override
    public CompletableFuture<Void> adjustStock(AdjustStockCommand command) {
        return inventoryService.adjustStock(command);
    }
}
