package com.example.demo.iface.rest;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.omni.payment.api.query.GetOrderPaymentsQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.command.CancelOrderCommand;
import com.example.demo.application.command.ConfirmOrderShipmentCommand;
import com.example.demo.application.command.CreateOrderCommand;
import com.example.demo.application.command.ReturnOrderCommand;
import com.example.demo.application.query.FindAllOrdersQuery;
import com.example.demo.application.query.GetOrderQuery;
import com.example.demo.application.service.OrderCommandService;
import com.example.demo.application.service.OrderQueryService;
import com.example.demo.iface.dto.req.CancelOrderResource;
import com.example.demo.iface.dto.req.ConfirmShipmentResource;
import com.example.demo.iface.dto.req.CreateOrderResource;
import com.example.demo.iface.dto.req.ReturnOrderResource;
import com.example.demo.iface.dto.res.OrderCancelledResource;
import com.example.demo.iface.dto.res.OrderCreatedResource;
import com.example.demo.iface.dto.res.OrderQueriedResource;
import com.example.demo.iface.dto.res.OrderReturnedResource;
import com.example.demo.iface.dto.res.OrderShipmentConfirmedResource;
import com.example.demo.iface.dto.res.OrdersQueriedResource;
import com.example.demo.iface.dto.res.PaymentsQueriedResource;
import com.example.demo.infra.mapper.OrderMapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

	private final OrderMapper orderMapper;
	private final OrderQueryService queryService;
	private final OrderCommandService commandService;

	/**
	 * 建立訂單
	 */
	@PostMapping("")
	public CompletableFuture<ResponseEntity<OrderCreatedResource>> createOrder(
			@RequestBody CreateOrderResource resource) {
		CreateOrderCommand command = orderMapper.transformACL(resource);
		String orderId = command.orderId();
		return commandService.createOrder(command).thenApply(result -> ResponseEntity.status(HttpStatus.CREATED)
				.body(new OrderCreatedResource("200", "建立訂單成功", orderId))).exceptionally(ex -> {
					log.error("[API] 建立訂單失敗: {}", ex.getMessage());
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new OrderCreatedResource("400", "參數錯誤 " + ex.getCause().getMessage(), null));
				});
	}

	/**
	 * 訂單出貨 API
	 */
	@PutMapping("/{orderId}/ship")
	public CompletableFuture<ResponseEntity<OrderShipmentConfirmedResource>> confirmShipment(
			@PathVariable String orderId, @RequestBody ConfirmShipmentResource resource) {
		ConfirmOrderShipmentCommand command = new ConfirmOrderShipmentCommand(orderId, resource.version());

		return commandService.confirmShipment(command)
				.thenApply(result -> ResponseEntity.ok(new OrderShipmentConfirmedResource("200", "出貨成功", orderId)))
				.exceptionally(ex -> {
					Throwable cause = (ex.getCause() != null) ? ex.getCause() : ex;

					// 1. 處理併發衝突
					if (cause instanceof org.axonframework.modelling.command.ConcurrencyException) {
						return ResponseEntity.status(HttpStatus.CONFLICT)
								.body(new OrderShipmentConfirmedResource("409", "出貨失敗：訂單已被更新，請重新操作", orderId));
					}

					// 2. 處理業務例外
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new OrderShipmentConfirmedResource("400", "出貨失敗: " + cause.getMessage(), orderId));
				});

	}

	/**
	 * 取消訂單 API
	 * <p>
	 * 業務規則：已出貨不可取消，已取消不可再次取消
	 * </p>
	 */
	@PutMapping("/{orderId}/cancel")
	public CompletableFuture<ResponseEntity<OrderCancelledResource>> cancelOrder(@PathVariable String orderId,
			@RequestBody CancelOrderResource resource) {

		log.info("[API] 收到訂單取消請求: {}, 預期版本: {}", orderId, resource.version());

		// 發送取消訂單命令
		CancelOrderCommand command = new CancelOrderCommand(orderId, resource.version());
		return commandService.cancelOrder(command)
				.thenApply(result -> ResponseEntity.ok(new OrderCancelledResource("200", "訂單已取消並進入退款流程", orderId)))
				.exceptionally(ex -> {
					// 由 Aggregate 拋出 IllegalStateException (例如已出貨等無法取消)
					log.error("[API] 取消訂單失敗: {}", ex.getMessage());
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new OrderCancelledResource("400", "無法取消訂單: " + ex.getCause().getMessage(), orderId));
				});
	}

	/**
	 * API: 訂單退貨
	 */
	@PutMapping("/{orderId}/return")
	public CompletableFuture<ResponseEntity<OrderReturnedResource>> returnOrder(@PathVariable String orderId,
			@RequestBody ReturnOrderResource resource) {

		log.info("[API] 收到退貨請求 OrderId={}, Version={}", orderId, resource.version());

		ReturnOrderCommand command = new ReturnOrderCommand(orderId, resource.version(), resource.reason());

		return commandService.returnOrder(command)
				.thenApply(result -> ResponseEntity.ok(new OrderReturnedResource("200", "訂單退貨成功", orderId)));
	}

	/**
	 * API: 查詢訂單列表
	 */
	@GetMapping("/list")
	public CompletableFuture<ResponseEntity<OrdersQueriedResource>> findAll() {
		return queryService.getAllOrders(new FindAllOrdersQuery())
				.thenApply(list -> new OrdersQueriedResource("200", "查詢成功", list)).thenApply(ResponseEntity::ok)
				.exceptionally(ex -> {
					// 處理例外 (回傳 500 Error)
					OrdersQueriedResource errorRes = new OrdersQueriedResource("500", "Error: " + ex.getMessage(),
							new ArrayList<>());
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRes);
				});
	}

	/**
	 * API: 查詢單一訂單詳細
	 */
	@GetMapping("/{orderId}")
	public CompletableFuture<ResponseEntity<OrderQueriedResource>> getOrder(@PathVariable String orderId) {
		GetOrderQuery query = new GetOrderQuery(orderId);
		return queryService.getOrder(query).thenApply(data -> new OrderQueriedResource("200", "查詢成功", data))
				.thenApply(ResponseEntity::ok).exceptionally(ex -> {
					OrderQueriedResource errorRes = new OrderQueriedResource("500", "Error: " + ex.getMessage(), null);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRes);
				});
	}

	/**
	 * API: 查詢單一訂單的支付紀錄
	 */
	@GetMapping("/{orderId}/payments")
	public CompletableFuture<ResponseEntity<PaymentsQueriedResource>> getPayments(@PathVariable String orderId) {
		return queryService.getPayments(new GetOrderPaymentsQuery(orderId))
				.thenApply(payments -> new PaymentsQueriedResource("200", "查詢成功", payments))
				.thenApply(ResponseEntity::ok).exceptionally(ex -> {
					PaymentsQueriedResource errorRes = new PaymentsQueriedResource("500", "Error: " + ex.getMessage(),
							null);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorRes);
				});
	}
}
