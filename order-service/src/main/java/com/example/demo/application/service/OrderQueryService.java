package com.example.demo.application.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import com.example.demo.application.query.FindAllOrdersQuery;
import com.example.demo.application.query.GetOrderQuery;
import com.omni.order.api.dto.OrderQueriedView;
import com.omni.payment.api.dto.PaymentQueriedView;
import com.omni.payment.api.query.GetOrderPaymentsQuery;

import lombok.RequiredArgsConstructor;

/**
 * OrderQueryService - 訂單查詢應用服務
 *
 * <p>
 * 這是系統的 Application Layer 的查詢層入口 (Facade)，負責透過 Axon 的 {@link QueryGateway}
 * 發送查詢命令 (Query Message) 並等待查詢模型的結果。
 * </p>
 *
 * <h3>主要職責：</h3>
 * <ul>
 * <li><b>封裝查詢邏輯：</b> 隱藏底層查詢機制，直接回傳 {@link CompletableFuture} 供展示層 API 異步或同步調用。</li>
 * <li><b>型別安全轉換：</b> 透過 {@link ResponseTypes} 將查詢結果安全地轉型為預期的 DTO 型別，確保結果正確。</li>
 * <li><b>聚合查詢結果：</b> 作為查詢端單一入口，集中處理多種類型的付款紀錄查詢請求。</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class OrderQueryService {

	/**
	 * Axon 查詢網關，負責將查詢事件派發給對應的 @QueryHandler
	 */
	private final QueryGateway queryGateway;

	/**
	 * 查詢並列出系統中所有的訂單列表
	 *
	 * <p>
	 * 派發 {@link FindAllOrdersQuery} 查詢，預期獲得多個 {@link OrderQueriedView} 實例列表。
	 * </p>
	 *
	 * @param query 查詢所有的請求事件
	 * @return 包含多個結果 DTO 實例的異步回傳。
	 */
	public CompletableFuture<List<OrderQueriedView>> getAllOrders(FindAllOrdersQuery query) {
		// 使用 multipleInstancesOf 告訴 Axon 預期回傳的是包含多個實例的列表
		return queryGateway.query(query, ResponseTypes.multipleInstancesOf(OrderQueriedView.class));
	}

	/**
	 * 根據訂單 ID 查詢單一訂單詳細資料
	 *
	 * <p>
	 * 帶有單一訂單 ID 的請求，預期回傳單一的訂單視圖。
	 * </p>
	 *
	 * @param query 包含單一 OrderId 的查詢物件
	 * @return 包含單一訂單 DTO 實例的異步回傳。
	 */
	public CompletableFuture<OrderQueriedView> getOrder(GetOrderQuery query) {
		// 使用 instanceOf 告訴 Axon 預期回傳的是單一實例
		return queryGateway.query(query, ResponseTypes.instanceOf(OrderQueriedView.class));
	}

	/**
	 * 查詢單一訂單相關聯的所有付款紀錄
	 *
	 * <p>
	 * 通常用於查詢介面，或是讓 Saga 流程中確認該筆訂單的付款紀錄。
	 * </p>
	 *
	 * @param query 包含單一 OrderId 的付款查詢物件
	 * @return 包含多個紀錄 DTO 實例的異步回傳。
	 */
	public CompletableFuture<List<PaymentQueriedView>> getPayments(GetOrderPaymentsQuery query) {
		// 透過 QueryGateway 將查詢派發給處理付款視圖的 Handler
		return queryGateway.query(query, ResponseTypes.multipleInstancesOf(PaymentQueriedView.class));
	}
}
