package com.omni.payment.infra.processor;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.omni.payment.api.dto.PaymentQueriedView;
import com.omni.payment.api.query.GetOrderPaymentsQuery;
import com.omni.payment.infra.persistence.PaymentViewRepository;

import lombok.AllArgsConstructor;

/**
 * PaymentProjectionHandler - 負責處理查詢投影視圖
 *
 * <p>
 * 此類別隸屬於六角形架構中的 Driven Adapter (輸出埠適配器)，負責監聽領域模型發出的領域事件 (Domain
 * Events)，並將狀態同步至 MySQL 資料庫中的 PaymentView 讀取模型。
 * </p>
 * <h3>設計特色：</h3>
 * <ul>
 * <li><b>充血模型呼叫：</b> 透過呼叫 PaymentView 的建構與業務方法 (如
 * markProcessed)，確保狀態變更邏輯封裝在實體中。</li>
 * <li><b>最終一致性：</b> 確保領域事件發佈到 Event Store 條件下，即使延遲也能在查詢端呈現。</li>
 * </ul>
 */
@Component
@AllArgsConstructor
public class PaymentProjectionHandler {

	private final PaymentViewRepository repository;

	@QueryHandler
	public List<PaymentQueriedView> handle(GetOrderPaymentsQuery query) {
		return repository.findByOrderId(query.orderId()).stream()
				.map(view -> new PaymentQueriedView(view.getPaymentId(), view.getOrderId(), view.getAmount(),
						view.getStatus()))
				.collect(Collectors.toList());
	}
}
