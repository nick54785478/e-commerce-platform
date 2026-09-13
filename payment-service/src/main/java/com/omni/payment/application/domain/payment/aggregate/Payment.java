package com.omni.payment.application.domain.payment.aggregate;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateVersion;
import org.axonframework.spring.stereotype.Aggregate;

import com.omni.payment.api.command.CancelPaymentCommand;
import com.omni.payment.api.command.CreatePaymentCommand;
import com.omni.payment.api.command.ProcessPaymentCommand;
import com.omni.payment.api.command.RefundPaymentCommand;
import com.omni.payment.api.event.PaymentCancelledEvent;
import com.omni.payment.api.event.PaymentCreatedEvent;
import com.omni.payment.api.event.PaymentProcessedEvent;
import com.omni.payment.api.event.PaymentRefundedEvent;
import com.omni.payment.api.exception.InsufficientFundsException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Payment Aggregate Root - 付款聚合根
 *
 * <p>
 * 負責處理與金流相關的業務邏輯，包含付款意圖的建立、扣款、以及後續的取消與退款流程。
 * </p>
 */
@Slf4j
@Aggregate
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	@AggregateIdentifier
	private String paymentId;

	/**
	 * 樂觀鎖版本控制，負責防止併發更新導致資料遺失
	 */
	@AggregateVersion
	private Long version;

	private String status;

	private BigDecimal amount;

	// ##### 1. 正常支付流程 (建立但未扣款) #####

	@CommandHandler
	public Payment(CreatePaymentCommand command) {
		log.info("[Payment] 建立付款紀錄: {}, 金額: {}", command.paymentId(), command.amount());
		AggregateLifecycle.apply(new PaymentCreatedEvent(command.paymentId(), command.orderId(), command.amount()));
	}

	/**
	 * 處理付款扣款命令
	 * 
	 * <pre>
	 * 業務規則：
	 * 	1. 檢查狀態：必須是 CREATED。
	 * 	2. 檢查金額：傳入的金額必須跟建立時的金額相符。
	 * 	3. 餘額/授權檢查：模擬金流閘道的回應，大額交易會失敗。
	 * </pre>
	 */
	@CommandHandler
	public void handle(ProcessPaymentCommand command) {
		log.info("[Payment] 處理付款扣款: {}", command.paymentId());

		// 規則 1：檢查狀態
		if (!"CREATED".equals(this.status)) {
			throw new IllegalStateException("無效狀態：當前狀態為 " + this.status);
		}

		// 規則 2：金額一致性校驗
		// 使用 compareTo == 0 來比較 BigDecimal，避免浮點數精確度差異
		if (this.amount == null || this.amount.compareTo(command.amount()) != 0) {
			log.error("[Payment] 金額不符，系統金額: {}, 實際傳入: {}", this.amount, command.amount());
			throw new IllegalArgumentException("扣款失敗：傳入金額 (" + command.amount() + ") 與系統金額 (" + this.amount + ") 不符");
		}

		// 規則 3：模擬授權檢查 (保護機制)
		// 這裡模擬：若金額大於 100,000，則判斷餘額不足拒絕交易
		if (command.amount().compareTo(new BigDecimal("100000")) > 0) {
			throw new InsufficientFundsException("扣款失敗：餘額不足或超過交易限額 (10 萬)");
		}

		// 驗證通過，發佈扣款成功事件
		AggregateLifecycle.apply(new PaymentProcessedEvent(command.paymentId(), command.orderId(), this.amount));
	}

	// ##### 2. 異常與退款流程 (Saga 補償) #####

	/**
	 * 取消付款命令 (尚未扣款)
	 */
	@CommandHandler
	public void handle(CancelPaymentCommand command) {
		// 冪等性控制：如果已經取消或退款，則直接忽略
		if ("CANCELLED".equals(this.status) || "REFUNDED".equals(this.status)) {
			return;
		}

		// 如果已經扣款完成，就不能直接取消，必須走退款流程
		if ("PROCESSED".equals(this.status)) {
			log.warn("[Payment] 付款已經完成扣款，不能直接取消，需走退款流程");
			return;
		}

		AggregateLifecycle.apply(new PaymentCancelledEvent(command.paymentId(), command.orderId()));
	}

	/**
	 * 處理退款命令 (已扣款)
	 */
	@CommandHandler
	public void handle(RefundPaymentCommand command) {
		log.info("[Payment] 準備退款: {}, 當前狀態: {}", command.paymentId(), this.status);

		// 1. 冪等性控制：如果已經退款，則忽略此次請求避免重複退款
		if ("REFUNDED".equals(this.status)) {
			log.info("[Payment] 該支付已經成功退款，忽略此次請求");
			return;
		}

		// 2. 狀態校驗：必須是已經扣款 (PROCESSED) 的支付才能退款
		if (!"PROCESSED".equals(this.status)) {
			throw new IllegalStateException("退款失敗：當前狀態為 " + this.status + "，只有已完成扣款的支付可以退款");
		}

		// 3. 發起退款事件
		AggregateLifecycle.apply(new PaymentRefundedEvent(command.paymentId(), command.orderId(), this.amount));
	}

	// ##### 3. 事件溯源處理 (Event Sourcing Handlers) #####

	@EventSourcingHandler
	public void on(PaymentCreatedEvent event) {
		this.paymentId = event.paymentId();
		this.amount = event.amount();
		this.status = "CREATED";
	}

	@EventSourcingHandler
	public void on(PaymentProcessedEvent event) {
		this.status = "PROCESSED";
	}

	@EventSourcingHandler
	public void on(PaymentCancelledEvent event) {
		this.status = "CANCELLED";
	}

	@EventSourcingHandler
	public void on(PaymentRefundedEvent event) {
		this.status = "REFUNDED";
	}
}
