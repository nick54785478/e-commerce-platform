package com.example.demo.infra.processor.product;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.SequenceNumber;
import org.springframework.stereotype.Component;

import com.omni.product.api.event.ProductCreatedEvent;
import com.example.demo.application.domain.product.event.ProductUpdatedEvent;
import com.example.demo.application.domain.product.event.ProductStatusChangedEvent;
import com.omni.inventory.api.event.StockAddedEvent;
import com.omni.inventory.api.event.StockReducedEvent;
import com.omni.inventory.api.event.StockAdjustedEvent;
import com.omni.inventory.api.event.StockReservedEvent;
import com.omni.inventory.api.event.StockReservationCancelledEvent;
import com.omni.product.api.enums.ProductStatus;
import com.example.demo.infra.projection.product.repository.ProductViewRepository;
import com.example.demo.infra.projection.product.entity.ProductView;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProductProjectionHandler - 商品查詢投影處理器
 * <p>
 * 屬於 CQRS 架構中的 Event Handler，負責接收 Event Bus 傳遞的事件，
 * 並將狀態同步更新至查詢端的關聯式資料庫 (MySQL/PostgreSQL) 中。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ProcessingGroup("product-group")
public class ProductProjectionHandler {

	private final ProductViewRepository repository;

	/**
	 * 處理商品建立事件
	 * 
	 * @param event   商品建立事件
	 * @param version 事件版本號
	 */
	@EventHandler
	public void on(ProductCreatedEvent event, @SequenceNumber Long version) {
		ProductView view = new ProductView();
		view.setProductId(event.productId());
		view.setTenantId(event.tenantId());
		view.markCreated(event.name(), event.description(), event.type(), event.subType(), event.price(), version);
		view.setImageUrls(event.imageUrls());
		view.setTags(event.tags());
		view.setStatus(ProductStatus.ACTIVE); // 預設狀態為上架
		repository.save(view);
	}

	/**
	 * 處理商品更新事件
	 */
	@EventHandler
	public void on(ProductUpdatedEvent event, @SequenceNumber Long version) {
		repository.findById(event.productId()).ifPresent(view -> {
			view.updateInfo(event.name(), event.description(), event.type(), event.subType(), event.price(), version);
			view.setImageUrls(event.imageUrls());
			view.setTags(event.tags());
			repository.save(view);
		});
	}

	/**
	 * 處理商品狀態變更 (上下架)
	 */
	@EventHandler
	public void on(ProductStatusChangedEvent event, @SequenceNumber Long version) {
		log.info("[Projection] 變更商品狀態 {} -> {}", event.productId(), event.status());
		repository.findById(event.productId()).ifPresent(view -> {
			view.setStatus(event.status());
			view.setVersion(version);
			repository.save(view);
		});
	}

	/**
	 * 處理扣減庫存事件
	 */
	@EventHandler
	public void on(StockReducedEvent event, @SequenceNumber Long version) {
		repository.findById(event.productId()).ifPresent(view -> {
			view.reduceStock(event.quantity(), version);
			repository.save(view);
		});
	}

	/**
	 * 處理庫存鎖定事件 (扣減可用庫存)
	 */
	@EventHandler
	public void on(StockReservedEvent event, @SequenceNumber Long version) {
		String originalProductId = event.productId().replace("INV-", "");
		repository.findById(originalProductId).ifPresent(view -> {
			view.reduceStock(event.quantity(), version);
			repository.save(view);
		});
	}

	/**
	 * 處理庫存鎖定取消事件 (加回可用庫存)
	 */
	@EventHandler
	public void on(StockReservationCancelledEvent event, @SequenceNumber Long version) {
		String originalProductId = event.productId().replace("INV-", "");
		repository.findById(originalProductId).ifPresent(view -> {
			view.addStock(event.quantity(), version);
			repository.save(view);
		});
	}

	/**
	 * 處理增加庫存事件 (補償機制)
	 */
	@EventHandler
	public void on(StockAddedEvent event, @SequenceNumber Long version) {
		String originalProductId = event.productId().replace("INV-", "");
		repository.findById(originalProductId).ifPresent(view -> {
			view.addStock(event.quantity(), version);
			repository.save(view);
		});
	}

	@EventHandler
	public void on(StockAdjustedEvent event, @SequenceNumber Long version) {
		String originalProductId = event.productId().replace("INV-", "");
		repository.findById(originalProductId).ifPresent(view -> {
			view.addStock(event.quantity(), version);
			repository.save(view);
		});
	}
}
