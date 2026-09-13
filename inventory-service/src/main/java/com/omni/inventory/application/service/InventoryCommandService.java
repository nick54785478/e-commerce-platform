package com.omni.inventory.application.service;

import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import com.omni.inventory.api.command.AddStockCommand;
import com.omni.inventory.api.command.CreateInventoryItemCommand;
import com.omni.inventory.api.command.ReduceStockCommand;
import com.omni.inventory.api.command.ReserveStockCommand;
import com.omni.inventory.api.command.ConfirmStockReservationCommand;
import com.omni.inventory.api.command.CancelStockReservationCommand;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * InventoryCommandService
 */
@Slf4j
@Service
@AllArgsConstructor
public class InventoryCommandService {

	private final CommandGateway commandGateway;

	public CompletableFuture<Void> reduceStock(String productId, String orderId, Integer quantity) {
		log.info("[Inventory Service] 接收手動扣減庫存請求: Product={}, Quantity={}", productId, quantity);
		return commandGateway.send(new ReduceStockCommand(productId, orderId, quantity));
	}

	public CompletableFuture<Void> addStock(String productId, String orderId, Integer quantity) {
		log.info("[Inventory Service] 接收手動增加庫存請求: Product={}, Quantity={}", productId, quantity);
		return commandGateway.send(new AddStockCommand(productId, orderId, quantity));
	}

	public CompletableFuture<?> initializeInventory(String productId, Integer initialStock) {
		log.info("[Inventory Service] 手動初始化庫存: Product={}, InitialStock={}", productId, initialStock);
		return commandGateway.send(new CreateInventoryItemCommand(productId, initialStock));
	}

	public CompletableFuture<Void> reserveStock(String productId, String orderId, Integer quantity) {
		log.info("[Inventory Service] 接收鎖定庫存請求: Product={}, Order={}, Quantity={}", productId, orderId, quantity);
		return commandGateway.send(new ReserveStockCommand(productId, orderId, quantity));
	}

	public CompletableFuture<Void> confirmReservation(String productId, String orderId) {
		log.info("[Inventory Service] 接收確認扣減庫存請求: Product={}, Order={}", productId, orderId);
		return commandGateway.send(new ConfirmStockReservationCommand(productId, orderId));
	}

	public CompletableFuture<Void> cancelReservation(String productId, String orderId, Integer quantity) {
		log.info("[Inventory Service] 接收釋放鎖定庫存請求: Product={}, Order={}, Quantity={}", productId, orderId, quantity);
		return commandGateway.send(new CancelStockReservationCommand(productId, orderId, quantity));
	}
}
