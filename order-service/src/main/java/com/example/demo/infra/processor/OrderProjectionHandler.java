package com.example.demo.infra.processor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OrderProjectionHandler - 負責訂單查詢投影處理
 *
 * <p>
 * 這個類別隸屬於六角形架構的 Driven Adapter (輸出埠適配器)，
 * 負責監聽訂單領域產生的事件 (Domain Events)，並同步到關聯式資料庫的 View 表。
 * </p>
 *
 * <h3>架構設計亮點：</h3>
 * <ul>
 * <li><b>讀寫分離 (CQRS)</b>: 將查詢邏輯與指令邏輯物理隔離，讀取端不需要理會領域模型的複雜規則。</li>
 * <li><b>防禦性寫入</b>: 在處理投影時加上防空指標處理，增加穩定度。</li>
 * <li><b>樂觀鎖同步 (Sequence Number)</b>: 確保 View 端的資料庫版本跟著 Event Store 推進。</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ProcessingGroup("order-group") // 指定處理群組，用來管理 Tracking Token 與並行處理設定
public class OrderProjectionHandler {

	private final OrderViewRepository repository;

	/**
	 * 處理訂單建立事件 (OrderCreatedEvent)
	 * <p>
	 * 當新訂單從 Aggregate 中被成功建立時觸發，於此投影模型寫入初始狀態。
	 * </p>
	 *
	 * @param event   訂單建立事實，包含訂單號、金額與品項資料
	 * @param version 來自 Axon Event Store 的版本號 (通常為 0)
	 */
	@EventHandler
	public void on(OrderCreatedEvent event, @SequenceNumber Long version) {
		log.info("[Projection] 接收到訂單建立事件 {}, 版本同步: {}", event.orderId(), version);

		OrderView view = new OrderView();
		view.setOrderId(event.orderId());
		view.setAmount(event.amount());
		view.setStatus("CREATED");
		view.setVersion(version); // 冪等控制：同步寫入端初始版本號

		// --- 狀態轉換與防禦性設計 ---
		// 確保品項清單不為 null，進行安全的轉換與映射實作
		List<OrderItemView> itemViews = Optional.ofNullable(event.items()).orElse(Collections.emptyList()).stream()
				.map(item -> new OrderItemView(item.productId(), item.quantity(), item.price())).toList();

		view.setItems(itemViews);

		repository.save(view);
		log.debug("[Projection] OrderView 寫入完成: {}", event.orderId());
	}

	/**
	 * 處理訂單取消事件 (OrderCancelledEvent)
	 * <p>
	 * 同步更新讀取端狀態為 CANCELLED。此操作可能由手動取消、庫存失敗或支付超時觸發。
	 * </p>
	 *
	 * @param event   訂單取消事件
	 * @param version 該操作產生的事件序號版本
	 */
	@EventHandler
	public void on(OrderCancelledEvent event, @SequenceNumber Long version) {
		log.info("[Projection] 更新訂單狀態為取消: {}, 版本號: {}", event.orderId(), version);

		repository.findById(event.orderId()).ifPresentOrElse(view -> {
			view.markCancelled(); // 呼叫充血模型的內部業務方法
			view.setVersion(version); // 更新版本以配合前端樂觀鎖
			repository.save(view);
		}, () -> log.warn("[Projection] 找不到欲更新的訂單資料(可能是競爭條件): {}", event.orderId()));
	}

	/**
	 * 處理通知出貨事件 (OrderNotifiedEvent)
	 * <p>
	 * 當支付模組回報成功扣款後，由 Saga 觸發。此狀態代表訂單已進入待出貨階段。
	 * </p>
	 *
	 * @param event   通知出貨事件
	 * @param version 該操作產生的事件序號版本
	 */
	@EventHandler
	public void on(OrderNotifiedEvent event, @SequenceNumber Long version) {
		log.info("[Projection] 訂單已支付並通知出貨: {}, 版本號: {}", event.orderId(), version);

		repository.findById(event.orderId()).ifPresent(view -> {
			view.markNotified();
			view.setVersion(version);
			repository.save(view);
		});
	}

	/**
	 * 處理訂單出貨完成事件 (OrderShippedEvent)
	 * <p>
	 * 同步狀態確認出貨完成。出貨後，前端將禁止進行「取消」操作。
	 * </p>
	 *
	 * @param event   出貨完成事件
	 * @param version 該操作產生的事件序號版本
	 */
	@EventHandler
	public void on(OrderShippedEvent event, @SequenceNumber Long version) {
		log.info("[Projection] 訂單出貨完成狀態更新: {}, 版本號: {}", event.orderId(), version);

		repository.findById(event.orderId()).ifPresent(view -> {
			view.markShipped();
			view.setVersion(version);
			repository.save(view);
		});
	}

	/**
	 * 處理訂單退貨事件 (OrderReturnedEvent)
	 * <p>
	 * 逆向物流流程。當訂單狀態變更為「RETURNED」時，同步更新並疊加新版本號，前端可藉此狀態進行後續再次申訴或歸檔作業。
	 * </p>
	 * * @param event 退貨事實，包含退貨原因
	 *
	 * @param version 該操作產生的事件序號版本
	 */
	@EventHandler
	public void on(OrderReturnedEvent event, @SequenceNumber Long version) {
		log.warn("[Projection] 訂單退貨成功記錄: {}, 原因: {}, 版本號: {}", event.orderId(), event.reason(), version);

		repository.findById(event.orderId()).ifPresent(view -> {
			// 直接調用 Setter 或實體方法來更新狀態
			view.setStatus("RETURNED");
			view.setVersion(version);
			repository.save(view);
		});
	}
}
