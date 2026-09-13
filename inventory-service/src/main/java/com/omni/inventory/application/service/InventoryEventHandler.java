package com.omni.inventory.application.service;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.omni.inventory.api.command.CreateInventoryItemCommand;
import com.omni.product.api.event.ProductCreatedEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * InventoryEventHandler - 跨服務事件監聽器
 * <p>
 * 負責監聽來自其他微服務 (如 product-service) 的跨領域事件，並轉化為本服務的 Command。
 * </p>
 */
@Slf4j
@Component
@AllArgsConstructor
@ProcessingGroup("inventory-event-handler")
public class InventoryEventHandler {

	private final CommandGateway commandGateway;

	/**
	 * 監聽商品建立事件，連帶建立初始庫存。
	 *
	 * <p>
	 * 使用 sendAndWait 同步等待，確保 InventoryItem Aggregate 確實建立完畢
	 * 避免非同步送出造成事件已消費但庫存卻沒建立的問題。
	 * </p>
	 */
	@EventHandler
	public void on(ProductCreatedEvent event) {
		log.info("[Inventory] 接收到商品建立事件，準備初始化庫存為 0: ProductId={}",
				event.productId());

		try {
			// 加上 'INV-' 前綴，避免與 Product aggregate 共用相同的 AggregateIdentifier 導致 Event Stream 衝突
			commandGateway.sendAndWait(new CreateInventoryItemCommand("INV-" + event.productId(), 0));
			log.info("[Inventory] ✅ 庫存初始化成功: ProductId={}", event.productId());
		} catch (Exception e) {
			// 若 Aggregate 已存在（例如重播時重複建立），記錄警告但不中斷 Processor
			log.warn("[Inventory] ⚠️ 庫存初始化失敗（可能已存在），ProductId={}: {}",
					event.productId(), e.getMessage());
		}
	}
}
