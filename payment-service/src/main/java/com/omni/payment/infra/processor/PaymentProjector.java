package com.omni.payment.infra.processor;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.omni.payment.api.event.PaymentCancelledEvent;
import com.omni.payment.api.event.PaymentCreatedEvent;
import com.omni.payment.api.event.PaymentProcessedEvent;
import com.omni.payment.api.event.PaymentRefundedEvent;
import com.omni.payment.infra.persistence.PaymentViewRepository;
import com.omni.payment.infra.projection.payment.PaymentView;

import lombok.AllArgsConstructor;

/**
 * PaymentProjector - 負責監聽領域事件，將資料寫入查詢端 (Read Model) 資料庫
 *
 * <p>
 * 將此投影處理器設置為特定的 {@code @ProcessingGroup}，如此一來在系統維護時
 * 可以獨立重播 (Replay) 該視圖的歷史事件，而不影響其他模組。
 * </p>
 */
@Component
@AllArgsConstructor
@ProcessingGroup("payment-projector")
public class PaymentProjector {

	private final PaymentViewRepository repository;

	/**
	 * 處理付款建立事件 (PaymentCreatedEvent)
	 * <p>
	 * 接收到付款請求時，起始一筆新的 PaymentView 紀錄。
	 * </p>
	 * 
	 * @param event 包含付款 ID、訂單 ID 與金額的事件
	 */
	@EventHandler
	public void on(PaymentCreatedEvent event) {
		PaymentView view = new PaymentView();
		view.setPaymentId(event.paymentId());
		view.setOrderId(event.orderId());
		view.setAmount(event.amount());

		// 使用實體內部業務方法設定狀態
		view.markCreated();
		repository.save(view);
	}

	/**
	 * 處理付款完成事件 (PaymentProcessedEvent)
	 * <p>
	 * 當確認支付系統已經成功扣款後，將視圖更新為 PROCESSED。
	 * </p>
	 * 
	 * @param event 包含付款 ID 的事件
	 */
	@EventHandler
	public void on(PaymentProcessedEvent event) {
		// 尋找實體並更新狀態
		repository.findById(event.paymentId()).ifPresent(view -> {
			view.markProcessed();
			repository.save(view);
		});
	}

	/**
	 * 處理付款取消事件 (PaymentCancelledEvent)
	 * <p>
	 * 異常流程：1. 訂單超時，由 Saga 主動觸發。2. 使用者取消尚未扣款的訂單。
	 * </p>
	 * 
	 * @param event 包含付款 ID 的取消事件
	 */
	@EventHandler
	public void on(PaymentCancelledEvent event) {
		repository.findById(event.paymentId()).ifPresent(view -> {
			view.markCancelled();
			repository.save(view);
		});
	}

	/**
	 * 處理付款退款事件 (PaymentRefundedEvent)
	 * <p>
	 * 當後續出貨手續失敗或退貨請求時，將紀錄標記為 REFUNDED。
	 * </p>
	 * 
	 * @param event 包含付款 ID 與退款金額的事件
	 */
	@EventHandler
	public void on(PaymentRefundedEvent event) {
		repository.findById(event.paymentId()).ifPresent(view -> {
			view.markRefunded();
			repository.save(view);
		});
	}
}
