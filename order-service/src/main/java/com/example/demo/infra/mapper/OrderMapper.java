package com.example.demo.infra.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.application.domain.order.aggregate.vo.OrderItem;
import com.example.demo.application.command.CreateOrderCommand;
import com.example.demo.iface.dto.req.CreateOrderItemResource;
import com.example.demo.iface.dto.req.CreateOrderResource;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.application.port.out.ProductServicePort;
import com.omni.product.api.dto.ProductQueriedView;

/**
 * OrderMapper - 負責將外部 API 的 Resource 轉換成系統內部的 Command
 */
@Component
public class OrderMapper implements BaseDataTransform<CreateOrderResource, CreateOrderCommand> {

	@Autowired
	private ProductServicePort productServicePort;

	@Override
	public CreateOrderCommand transformACL(CreateOrderResource source) {
		List<OrderItem> items = source.items().stream()
				.map(item -> new OrderItem(item.productId(), item.quantity(), calculatePrice(item))).toList();
		BigDecimal amount = items.stream().map(item -> item.price().multiply(new BigDecimal(item.quantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new CreateOrderCommand(null, items, amount);
	}

	private BigDecimal calculatePrice(CreateOrderItemResource item) {
		try {
			// 透過 Port 呼叫商品服務，與底層 HTTP 實作解耦
			ProductQueriedView product = productServicePort.getProduct(item.productId());
			
			if (product != null && product.price() != null) {
				return product.price();
			}
		} catch (Exception e) {
			System.err.println("Failed to fetch product price via Port: " + e.getMessage());
			e.printStackTrace();
		}
		return new BigDecimal("100"); // default fallback
	}
}
