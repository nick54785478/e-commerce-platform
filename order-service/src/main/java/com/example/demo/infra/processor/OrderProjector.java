package com.example.demo.infra.processor;

import java.util.List;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.SequenceNumber;
import org.springframework.stereotype.Component;

import com.example.demo.application.domain.order.event.OrderCancelledEvent;
import com.example.demo.application.domain.order.event.OrderCreatedEvent;
import com.example.demo.application.domain.order.event.OrderNotifiedEvent;
import com.example.demo.application.domain.order.event.OrderReturnedEvent;
import com.example.demo.application.domain.order.event.OrderShippedEvent;
import com.example.demo.infra.persistence.OrderViewRepository;
import com.example.demo.infra.projection.order.OrderItemView;
import com.example.demo.infra.projection.order.OrderView;

import lombok.AllArgsConstructor;

/**
 * OrderProjector - 負責將領域事件投影到 Read Model (View) 中
 */
@Component
@AllArgsConstructor
@ProcessingGroup("order-group") // 指定 Event Processor 的 Group Name
public class OrderProjector {

	private final OrderViewRepository repository;

	@EventHandler
	public void on(OrderCreatedEvent event, @SequenceNumber Long version) {
		OrderView view = new OrderView();
		view.setOrderId(event.orderId());
		view.setAmount(event.amount());
		view.setStatus("CREATED");
		view.setVersion(version);

		List<OrderItemView> items = event.items().stream()
				.map(item -> new OrderItemView(item.productId(), item.quantity(), item.price())).toList();

		view.setItems(items);
		repository.save(view);
	}

	@EventHandler
	public void on(OrderNotifiedEvent event, @SequenceNumber Long version) {
		repository.findById(event.orderId()).ifPresent(view -> {
			view.markNotified();
			view.setVersion(version);
			repository.save(view);
		});
	}

	@EventHandler
	public void on(OrderShippedEvent event, @SequenceNumber Long version) {
		repository.findById(event.orderId()).ifPresent(view -> {
			view.markShipped();
			view.setVersion(version);
			repository.save(view);
		});
	}

	@EventHandler
	public void on(OrderCancelledEvent event, @SequenceNumber Long version) {
		repository.findById(event.orderId()).ifPresent(view -> {
			view.markCancelled();
			view.setVersion(version);
			repository.save(view);
		});
	}

	@EventHandler
	public void on(OrderReturnedEvent event, @SequenceNumber Long version) {
		repository.findById(event.orderId()).ifPresent(view -> {
			view.markReturned();
			view.setVersion(version);
			repository.save(view);
		});
	}
}
