package com.omni.payment.iface.rest;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.omni.payment.application.service.PaymentCommandService;
import com.omni.payment.api.exception.InsufficientFundsException;
import com.omni.payment.iface.dto.req.ProcessPaymentResource;
import com.omni.payment.iface.dto.res.PaymentProcessedResource;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

	private final PaymentCommandService paymentCommandService;

	@PutMapping("/{paymentId}/process")
	public CompletableFuture<ResponseEntity<PaymentProcessedResource>> processPayment(@PathVariable String paymentId,
			@RequestBody ProcessPaymentResource resource) {

		log.info("[API] 收到付款處理請求: PaymentId={}, Amount={}", paymentId, resource.amount());

		return paymentCommandService.processPayment(paymentId, resource.orderId(), resource.amount())
				.thenApply(result -> ResponseEntity.ok(new PaymentProcessedResource("200", "付款處理成功", paymentId)))
				.exceptionally(ex -> {
					Throwable cause = ex.getCause();
					log.error("[API] 付款處理失敗: {}", cause.getMessage());

					// 餘額不足回傳 402
					if (cause instanceof InsufficientFundsException) {
						return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
								.body(new PaymentProcessedResource("402", cause.getMessage(), paymentId));
					}

					// 其他業務邏輯錯誤 (如狀態錯誤等) 回傳 400
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(new PaymentProcessedResource("400", "付款處理失敗: " + cause.getMessage(), paymentId));
				});
	}
}
