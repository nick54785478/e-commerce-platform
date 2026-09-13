package com.example.demo.application.saga;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.axonframework.config.ProcessingGroup;

import com.example.demo.application.command.CancelOrderCommand;
import com.example.demo.application.command.NotifyShipmentCommand;
import com.example.demo.application.domain.order.aggregate.vo.OrderItem;
import com.example.demo.application.domain.order.event.OrderCancelledEvent;
import com.example.demo.application.domain.order.event.OrderCreatedEvent;
import com.example.demo.application.domain.order.event.OrderNotifiedEvent;
import com.example.demo.application.domain.order.event.OrderReturnedEvent;
import com.omni.payment.api.command.CancelPaymentCommand;
import com.omni.payment.api.command.CreatePaymentCommand;
import com.omni.payment.api.command.RefundPaymentCommand;
import com.omni.payment.api.event.PaymentProcessedEvent;
import com.omni.inventory.api.command.AddStockCommand;
import com.omni.inventory.api.command.ReserveStockCommand;
import com.omni.inventory.api.command.ConfirmStockReservationCommand;
import com.omni.inventory.api.command.CancelStockReservationCommand;
import com.omni.inventory.api.event.StockReservedEvent;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OrderManagementSaga - 訂單生命週期管理 (Saga 協調者)
 *
 * <p>
 * 這是一個典型的 Orchestration Saga 模式的實作，負責協調 Order, Inventory, 和 Payment 之間的分布式交易。
 * </p>
 */
@Saga
@Slf4j
@NoArgsConstructor
@ProcessingGroup("order-saga")
public class OrderManagementSaga {

	private String paymentId;

	private BigDecimal amount;

	private String orderId;

	private boolean paymentCompleted = false;

	private String paymentDeadlineId;

	private boolean isCancelling = false;

	private List<OrderItem> reservedItems = new ArrayList<>();

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderCreatedEvent event, CommandGateway commandGateway, DeadlineManager deadlineManager) {
		log.info("[Saga] 訂單 {} 建立，發起扣庫存命令並設定付款超時倒數", event.orderId());
		this.orderId = event.orderId();
		this.amount = event.amount();

		this.paymentDeadlineId = deadlineManager.schedule(Duration.ofMinutes(10), "payment-deadline", event);

		for (OrderItem item : event.items()) {
			// 發送鎖定庫存命令，由於 inventory-service 將 Aggregate Identifier 加上了 INV- 前綴以避免與 Product 衝突
			commandGateway.send(new ReserveStockCommand("INV-" + item.productId(), this.orderId, item.quantity()))
					.exceptionally(ex -> {
						synchronized (this) {
							if (!isCancelling) {
								isCancelling = true;
								log.error("[Saga] 鎖定庫存失敗 (商品: {})，發起取消訂單流程", item.productId());
								commandGateway.send(new CancelOrderCommand(this.orderId, null));
							}
						}
						return null;
					});
		}
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(StockReservedEvent event, CommandGateway commandGateway) {
		log.info("[Saga] 商品 {} 庫存鎖定完成", event.productId());
		this.reservedItems.add(new OrderItem(event.productId(), event.quantity(), BigDecimal.ZERO));

		synchronized (this) {
			if (this.paymentId == null && !isCancelling) {
				this.paymentId = UUID.randomUUID().toString();
				SagaLifecycle.associateWith("paymentId", this.paymentId);
				commandGateway.send(new CreatePaymentCommand(this.paymentId, this.orderId, this.amount));
			}
		}
	}

	@SagaEventHandler(associationProperty = "paymentId")
	public void on(PaymentProcessedEvent event, CommandGateway commandGateway, DeadlineManager deadlineManager) {
		log.info("[Saga] 付款處理完成，訂單 {}", event.orderId());
		this.paymentCompleted = true;

		// 付款完成，發送確認扣減庫存命令給所有已鎖定的商品
		for (OrderItem item : reservedItems) {
			commandGateway.send(new ConfirmStockReservationCommand(item.productId(), this.orderId));
		}

		if (this.paymentDeadlineId != null) {
			deadlineManager.cancelSchedule("payment-deadline", this.paymentDeadlineId);
		}
		commandGateway.send(new NotifyShipmentCommand(event.orderId()));
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderNotifiedEvent event) {
		log.info("[Saga] 訂單 {} 已通知出貨，Saga 流程結束", event.orderId());
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderCancelledEvent event, CommandGateway commandGateway) {
		log.warn("[Saga] 訂單 {} 已被取消，發起退款與補償流程...", event.orderId());
		performCompensation(commandGateway);
		SagaLifecycle.end();
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void on(OrderReturnedEvent event, CommandGateway commandGateway) {
		log.warn("[Saga] 訂單 {} 已被退貨，發起退款與庫存補償...", event.orderId());
		performCompensation(commandGateway);
		SagaLifecycle.end();
	}

	private void performCompensation(CommandGateway commandGateway) {
		this.isCancelling = true;

		if (!reservedItems.isEmpty()) {
			for (OrderItem item : reservedItems) {
				if (this.paymentCompleted) {
					log.info("[Saga] 補償：付款已完成，歸還商品 {} 的實體庫存(數量: {})", item.productId(), item.quantity());
					commandGateway.send(new AddStockCommand(item.productId(), this.orderId, item.quantity()));
				} else {
					log.info("[Saga] 補償：付款未完成，釋放商品 {} 的庫存鎖定", item.productId());
					commandGateway.send(new CancelStockReservationCommand(item.productId(), this.orderId, item.quantity()));
				}
			}
		}

		if (this.paymentId != null) {
			if (this.paymentCompleted) {
				log.info("[Saga] 補償：付款已完成，發起退款(PaymentId: {})", this.paymentId);
				commandGateway.send(new RefundPaymentCommand(this.paymentId, this.orderId, this.amount));
			} else {
				log.info("[Saga] 補償：付款未完成，直接取消支付(PaymentId: {})", this.paymentId);
				commandGateway.send(new CancelPaymentCommand(this.paymentId, this.orderId));
			}
		}
	}

	@DeadlineHandler(deadlineName = "payment-deadline")
	public void handlePaymentTimeout(OrderCreatedEvent event, CommandGateway commandGateway) {
		synchronized (this) {
			if (!isCancelling && !paymentCompleted) {
				this.isCancelling = true;
				log.warn("[Saga] 訂單 (OrderId: {}) 付款超時，發起取消訂單流程", event.orderId());
				commandGateway.send(new CancelOrderCommand(this.orderId, null));
			}
		}
	}
}
