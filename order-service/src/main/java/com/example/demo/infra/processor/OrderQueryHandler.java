package com.example.demo.infra.processor;

import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.example.demo.application.query.FindAllOrdersQuery;

import com.example.demo.application.query.GetOrderQuery;
import com.example.demo.infra.persistence.OrderViewRepository;

import com.example.demo.infra.projection.order.OrderView;

import lombok.AllArgsConstructor;

/**
 * OrderQueryHandler - 負責處理 Query 端查詢請求，從 Read Model 資料庫中撈取視圖資料
 */
@Component
@AllArgsConstructor
public class OrderQueryHandler {

	private final OrderViewRepository repository;

	@QueryHandler
	public OrderView handle(GetOrderQuery query) {
		return repository.findById(query.orderId())
				.orElseThrow(() -> new IllegalArgumentException("找不到該筆訂單: " + query.orderId()));
	}

	@QueryHandler
	public List<OrderView> handle(FindAllOrdersQuery query) {
		return repository.findAll();
	}


}
