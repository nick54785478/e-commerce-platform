package com.omni.inventory.iface.rest;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omni.inventory.application.service.InventoryCommandService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * InventoryController - 庫存管理 REST API
 */
@Slf4j
@RestController
@RequestMapping("/inventory")
@AllArgsConstructor
public class InventoryController {

	private final InventoryCommandService inventoryCommandService;

	/**
	 * 初始化商品庫存 (手動補建 InventoryItem Aggregate)
	 *
	 * <p>
	 * 當商品已在 product_view 存在，但 InventoryItem Aggregate 尚未建立時，
	 * 可呼叫此 API 補建庫存聚合根。
	 * </p>
	 *
	 * <pre>
	 * POST /inventory/init
	 * {
	 *   "productId": "xxx",
	 *   "initialStock": 100
	 * }
	 * </pre>
	 */
	@PostMapping("/init")
	public CompletableFuture<ResponseEntity<String>> initInventory(
			@RequestBody InitInventoryRequest request) {
		log.info("[InventoryController] 初始化庫存 API: productId={}, initialStock={}",
				request.productId(), request.initialStock());
		return inventoryCommandService.initializeInventory(request.productId(), request.initialStock())
				.thenApply(result -> ResponseEntity.ok(
						"庫存初始化成功: productId=" + request.productId()
								+ ", initialStock=" + request.initialStock()));
	}

	/**
	 * 手動增加庫存
	 *
	 * <pre>
	 * POST /inventory/{productId}/add?quantity=10
	 * </pre>
	 */
	@PostMapping("/{productId}/add")
	public CompletableFuture<ResponseEntity<String>> addStock(
			@PathVariable String productId,
			@RequestBody AddStockRequest request) {
		log.info("[InventoryController] 手動增加庫存: productId={}, quantity={}", productId, request.quantity());
		return inventoryCommandService.addStock(productId, "manual", request.quantity())
				.thenApply(result -> ResponseEntity.ok(
						"庫存增加成功: productId=" + productId + ", quantity=" + request.quantity()));
	}

	// -------- Request Records --------

	public record InitInventoryRequest(String productId, Integer initialStock) {
	}

	public record AddStockRequest(Integer quantity) {
	}
}
