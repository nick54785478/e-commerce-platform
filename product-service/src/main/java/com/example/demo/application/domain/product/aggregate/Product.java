package com.example.demo.application.domain.product.aggregate;

import java.math.BigDecimal;
import java.util.List;

import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateVersion;
import org.axonframework.spring.stereotype.Aggregate;

import com.omni.product.api.event.ProductCreatedEvent;
import com.example.demo.application.domain.product.event.ProductUpdatedEvent;
import com.example.demo.application.domain.product.event.ProductStatusChangedEvent;
import com.omni.product.api.enums.ProductStatus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Product Aggregate Root - 商品聚合根
 * <p>
 * 屬於 Domain Layer (領域層)，負責封裝商品的核心狀態與業務邏輯。
 * 遵循 DDD 原則，商品狀態的修改不直接依賴 @CommandHandler，而是由 Application Layer 呼叫。
 * 所有狀態的變更透過事件溯源 (Event Sourcing) 完成。
 * </p>
 */
@Slf4j
@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@AggregateIdentifier
	private String productId;

	private String tenantId;
	private String name;
	private String description;
	private String type;
	private String subType;

	private List<String> imageUrls;
	private List<String> tags;
	private BigDecimal price;
	private ProductStatus status;

	@AggregateVersion
	private Long version;

	// ##### 1. 初始化與工廠方法 (Initialization & Factory) #####

	private Product(String productId, String tenantId, String name, String description, String type, String subType, BigDecimal price, List<String> imageUrls, List<String> tags) {
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("商品價格必須大於零");
		}
		AggregateLifecycle.apply(new ProductCreatedEvent(productId, tenantId, name, description, type, subType, price, imageUrls, tags));
	}

	/**
	 * 工廠方法 - 建立商品聚合根
	 * <p>
	 * 提供給 Application Service 的 Axon Repository 呼叫。
	 * </p>
	 */
	public static Product create(String productId, String tenantId, String name, String description, String type, String subType, BigDecimal price, List<String> imageUrls, List<String> tags) {
		log.info("[Product Domain] 準備建立商品: {}", productId);
		return new Product(productId, tenantId, name, description, type, subType, price, imageUrls, tags);
	}

	// ##### 2. 業務邏輯執行 (Execution) #####

	/**
	 * 業務邏輯 - 變更商品狀態 (上下架)
	 */
	public void changeStatus(String tenantId, ProductStatus newStatus) {
		log.info("[Product Domain] 準備變更商品狀態: {} -> {}", this.productId, newStatus);
		if (this.status == newStatus) {
			return; // 狀態沒有改變，不需要觸發事件
		}
		AggregateLifecycle.apply(new ProductStatusChangedEvent(this.productId, tenantId, newStatus));
	}

	/**
	 * 業務邏輯 - 更新商品資訊
	 */
	public void update(String tenantId, String name, String description, String type, String subType, BigDecimal price, List<String> imageUrls, List<String> tags) {
		log.info("[Product Domain] 準備更新商品: {}, 目前版本: {}", this.productId, this.version);
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("更新失敗：價格必須大於零");
		}
		AggregateLifecycle.apply(new ProductUpdatedEvent(this.productId, tenantId, name, description, type, subType, price, imageUrls, tags));
	}

	// ##### 事件溯源處理 (Event Sourcing Handlers) #####

	@EventSourcingHandler
	protected void on(ProductCreatedEvent event) {
		this.tenantId = event.tenantId();
		this.productId = event.productId();
		this.name = event.name();
		this.description = event.description();
		this.type = event.type();
		this.subType = event.subType();
		this.price = event.price();

		this.imageUrls = event.imageUrls();
		this.tags = event.tags();
		this.status = ProductStatus.ACTIVE; // 預設建立即生效
	}

	@EventSourcingHandler
	protected void on(ProductStatusChangedEvent event) {
		this.status = event.status();
	}

	@EventSourcingHandler
	protected void on(ProductUpdatedEvent event) {
		this.tenantId = event.tenantId();
		this.name = event.name();
		this.description = event.description();
		this.type = event.type();
		this.subType = event.subType();
		this.price = event.price();
		this.imageUrls = event.imageUrls();
		this.tags = event.tags();
	}

	public String getProductId() {
		return productId;
	}
}
