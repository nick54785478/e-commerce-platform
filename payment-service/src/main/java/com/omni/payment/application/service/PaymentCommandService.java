package com.omni.payment.application.service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import com.omni.payment.application.domain.payment.aggregate.Payment;
import com.omni.payment.api.command.ProcessPaymentCommand;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * PaymentCommandService - 付款命令應用服務
 *
 * <p>
 * 負責接收來自外部或 Saga 的呼叫，並將「處理付款」命令派發給 {@link Payment} 聚合根。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class PaymentCommandService {

	private final CommandGateway commandGateway;

	/**
	 * 發起付款扣款流程
	 * <p>
	 * 即使在短時間內收到多次相同的扣款請求，聚合根也能透過狀態驗證與冪等性設計，確保付款只會被扣款一次。
	 * </p>
	 * 
	 * @param paymentId 系統內的付款唯一識別碼
	 * @param orderId   關聯的目標訂單唯一識別碼
	 * @param amount    本次應扣款的金額
	 * @return CompletableFuture&lt;Void&gt; 用於等待命令是否執行完成
	 */
	public CompletableFuture<Void> processPayment(String paymentId, String orderId, BigDecimal amount) {
		log.info("[Payment Service] 接收付款扣款請求: PaymentId={}, Amount={}", paymentId, amount);

		// 轉發扣款命令到 Payment Aggregate 執行付款邏輯
		return commandGateway.send(new ProcessPaymentCommand(paymentId, orderId, amount));
	}
}
