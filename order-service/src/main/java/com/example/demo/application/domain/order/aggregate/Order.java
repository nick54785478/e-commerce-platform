package com.example.demo.application.domain.order.aggregate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateVersion;
import org.axonframework.spring.stereotype.Aggregate;

import com.example.demo.application.domain.order.aggregate.vo.OrderItem;
import com.example.demo.application.domain.order.aggregate.vo.OrderStatus;
import com.example.demo.application.command.CancelOrderCommand;
import com.example.demo.application.command.ConfirmOrderShipmentCommand;
import com.example.demo.application.command.CreateOrderCommand;
import com.example.demo.application.command.NotifyShipmentCommand;
import com.example.demo.application.command.ReturnOrderCommand;
import com.example.demo.application.command.ShipOrderCommand;
import com.example.demo.application.domain.order.event.OrderCancelledEvent;
import com.example.demo.application.domain.order.event.OrderCreatedEvent;
import com.example.demo.application.domain.order.event.OrderNotifiedEvent;
import com.example.demo.application.domain.order.event.OrderReturnedEvent;
import com.example.demo.application.domain.order.event.OrderShippedEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Order Aggregate Root - 訂單聚合根
 *
 * <p>
 * 負責維護「訂單」這個領域物件的核心狀態。所有狀態變更（例如：建立、出貨、取消等）都必須透過發送指令給聚合根，再由其驗證規則並拋出事件來完成。
 * </p>
 */
@Slf4j
@Getter
@Aggregate(snapshotTriggerDefinition = "orderSnapshotTriggerDefinition")
public class Order {

	@AggregateIdentifier
	private String orderId;

	@AggregateVersion
	private Long version;

	private List<OrderItem> items;
	private BigDecimal amount;
	private OrderStatus status;

	protected Order() {
		// Required by Axon for Event Replay
	}

	// ##### 1. 初始化 (Initialization) #####

	@CommandHandler
	public Order(CreateOrderCommand command) {
		log.info("[Order] 接收到 CreateOrderCommand: {}", command.orderId());

		if (command.items() == null || command.items().isEmpty()) {
			throw new IllegalArgumentException("訂單至少需要包含一個商品項目");
		}

		BigDecimal calculatedAmount = command.items().stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		AggregateLifecycle.apply(new OrderCreatedEvent(command.orderId(), command.items(), calculatedAmount));
	}

	@EventSourcingHandler
	public void on(OrderCreatedEvent event) {
		this.orderId = event.orderId();
		this.items = new ArrayList<>(event.items());
		this.amount = event.amount();
		this.status = OrderStatus.CREATED;
	}

	// ##### 2. 狀態轉換與執行 (Execution & Transition) #####

	/**
	 * 確認物流出貨指令
	 * <p>
	 * 此方法由對外 API {@code /orders/{orderId}/ship} 呼叫，只有在「已通知物流
	 * (NOTIFIED)」狀態下，才能轉換為已出貨狀態。
	 * </p>
	 */
	@CommandHandler
	public void handle(ConfirmOrderShipmentCommand command) {
		log.info("[Order] 確認物流出貨指令: {}, Version: {}", command.orderId(), command.version());

		// 1. 冪等性控制
		if (this.status == OrderStatus.SHIPPED) {
			log.warn("[Order] 訂單 {} 已經出貨，忽略此請求", this.orderId);
			return;
		}

		// 2. 狀態機校驗：必須是已通知物流 (NOTIFIED) 狀態才能出貨
		if (this.status != OrderStatus.NOTIFIED) {
			throw new IllegalStateException("無法出貨：目前狀態為 " + this.status + "，必須先通知物流");
		}

		AggregateLifecycle.apply(new OrderShippedEvent(command.orderId()));
	}

	/**
	 * 系統/內部出貨指令 (無條件)
	 */
	@CommandHandler
	public void handle(ShipOrderCommand command) {
		if (this.status == OrderStatus.SHIPPED)
			return;
		if (this.status == OrderStatus.CANCELLED) {
			throw new IllegalStateException("訂單已取消，無法進行出貨");
		}
		AggregateLifecycle.apply(new OrderShippedEvent(command.orderId()));
	}

	/**
	 * 通知物流出貨指令 (通常由 Saga 支付成功後觸發)
	 */
	@CommandHandler
	public void handle(NotifyShipmentCommand command) {
		if (this.status != OrderStatus.CREATED) {
			throw new IllegalStateException("當前狀態為 " + this.status + "，不允許通知物流");
		}
		AggregateLifecycle.apply(new OrderNotifiedEvent(command.orderId()));
	}

	@EventSourcingHandler
	public void on(OrderShippedEvent event) {
		this.status = OrderStatus.SHIPPED;
	}

	@EventSourcingHandler
	public void on(OrderNotifiedEvent event) {
		this.status = OrderStatus.NOTIFIED;
	}

	// ##### 3. 取消與退貨處理 (Cancellation & Returns) #####

	@CommandHandler
	public void handle(CancelOrderCommand command) {
		log.info("[Order] 處理 CancelOrderCommand: {}, Version: {}", command.orderId(), command.version());

		if (this.status == OrderStatus.CANCELLED)
			return;

		if (this.status == OrderStatus.SHIPPED) {
			throw new IllegalStateException("已出貨訂單無法取消，請改用退貨流程");
		}

		AggregateLifecycle.apply(new OrderCancelledEvent(command.orderId()));
	}

	@CommandHandler
	public void handle(ReturnOrderCommand command) {
		log.info("[Order] 處理退貨要求: {}, 原因: {}", command.orderId(), command.reason());

		if (this.status == OrderStatus.RETURNED) {
			return;
		}

		if (this.status != OrderStatus.SHIPPED) {
			throw new IllegalStateException("無法退貨：當前狀態為 " + this.status + "，只有已出貨訂單可申請退貨");
		}

		AggregateLifecycle.apply(new OrderReturnedEvent(this.orderId, new ArrayList<>(this.items), command.reason()));
	}

	@EventSourcingHandler
	public void on(OrderCancelledEvent event) {
		this.status = OrderStatus.CANCELLED;
	}

	@EventSourcingHandler
	public void on(OrderReturnedEvent event) {
		this.status = OrderStatus.RETURNED;
	}
}
