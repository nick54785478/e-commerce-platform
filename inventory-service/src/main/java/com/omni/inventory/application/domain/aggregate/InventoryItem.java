package com.omni.inventory.application.domain.aggregate;

import com.omni.inventory.api.command.CreateInventoryItemCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateVersion;
import org.axonframework.spring.stereotype.Aggregate;

import com.omni.inventory.api.command.AddStockCommand;
import com.omni.inventory.api.command.ReduceStockCommand;
import com.omni.inventory.api.event.StockAddedEvent;
import com.omni.inventory.api.event.StockReducedEvent;
import com.omni.inventory.api.command.AdjustStockCommand;
import com.omni.inventory.api.event.StockAdjustedEvent;
import com.omni.inventory.api.command.ReserveStockCommand;
import com.omni.inventory.api.command.ConfirmStockReservationCommand;
import com.omni.inventory.api.command.CancelStockReservationCommand;
import com.omni.inventory.api.event.StockReservedEvent;
import com.omni.inventory.api.event.StockReservationConfirmedEvent;
import com.omni.inventory.api.event.StockReservationCancelledEvent;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * InventoryItem Aggregate Root - 庫存項目聚合根
 *
 * <p>
 * 負責處理特定商品的庫存增減邏輯。此聚合根不處理商品型錄資訊，僅專注於庫存水位管理。
 * </p>
 * <p>
 * <b>架構說明 (Axon Native Style)</b>：<br>
 * 本類別採用 Axon 官方推薦的原生寫法，將 {@code @CommandHandler} 與 {@code @EventSourcingHandler}
 * 都集中在 Aggregate 內部。這代表：
 * <ul>
 *   <li><b>高內聚</b>：指令處理規則、驗證與狀態變更都緊密地綁在 Aggregate 裡面。</li>
 *   <li><b>自動路由</b>：Axon Command Bus 會自動根據指令的 {@code @TargetAggregateIdentifier} 
 *       載入此實體，並呼叫對應的 {@code @CommandHandler} 方法。</li>
 * </ul>
 * </p>
 */
@Slf4j
@Aggregate
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {

	@AggregateIdentifier
	private String productId;

	@AggregateVersion
	private Long version;

	private Integer stock;

	private Map<String, Integer> reservations = new HashMap<>();

	/**
	 * 處理建立庫存項目的指令 (Constructor Command Handler)
	 * <p>
	 * 當收到建立指令時，這是 Aggregate 的起點。
	 * 在此進行必要驗證後，必須透過 {@code AggregateLifecycle.apply()} 發佈事件，
	 * 不可以直接在此處修改屬性狀態 (例如 this.stock = ...)。
	 * </p>
	 *
	 * @param command 建立庫存項目指令
	 */
	@CommandHandler
	public InventoryItem(CreateInventoryItemCommand command) {
		log.info("[InventoryItem] 建立庫存項目: {}, 初始庫存: {}", command.productId(), command.initialStock());
		AggregateLifecycle.apply(new com.omni.inventory.api.event.InventoryItemCreatedEvent(command.productId(), command.initialStock()));
	}

	/**
	 * 事件溯源處理器 (Event Sourcing Handler) - 處理建立事件
	 * <p>
	 * 當事件被發佈或系統重新載入 Aggregate 時呼叫。
	 * <b>重要規則</b>：這裡唯一該做的事就是「修改狀態」(Mutate State)。
	 * 絕對不可在此放入商業邏輯判斷或呼叫外部服務 (無副作用)。
	 * </p>
	 *
	 * @param event 庫存項目建立事件
	 */
	@EventSourcingHandler
	public void on(com.omni.inventory.api.event.InventoryItemCreatedEvent event) {
		this.productId = event.productId();
		this.stock = event.initialStock();
	}

	/**
	 * 處理扣減庫存指令
	 * <p>
	 * 在此方法內進行領域邏輯驗證 (Domain Logic Validation)。
	 * 如果庫存不足，拋出例外以阻斷操作；若驗證通過，則發佈庫存已扣減事件。
	 * </p>
	 *
	 * @param command 扣減庫存指令
	 */
	@CommandHandler
	public void handle(ReduceStockCommand command) {
		log.info("[InventoryItem] 準備扣減庫存: {}, 數量: {}", command.productId(), command.quantity());
		
		if (this.stock < command.quantity()) {
			log.error("[InventoryItem] 庫存不足: {}, 目前庫存: {}, 要求扣減: {}", command.productId(), this.stock, command.quantity());
			throw new IllegalStateException("庫存不足，無法完成扣減");
		}

		AggregateLifecycle.apply(new StockReducedEvent(command.productId(), command.orderId(), command.quantity()));
	}

	/**
	 * 處理增加庫存指令
	 *
	 * @param command 增加庫存指令
	 */
	@CommandHandler
	public void handle(AddStockCommand command) {
		log.info("[InventoryItem] 準備增加/歸還庫存: {}, 數量: {}", command.productId(), command.quantity());
		AggregateLifecycle.apply(new StockAddedEvent(command.productId(), command.orderId(), command.quantity()));
	}

	/**
	 * 處理人工調整庫存指令
	 *
	 * @param command 調整庫存指令
	 */
	@CommandHandler
	public void handle(AdjustStockCommand command) {
		log.info("[InventoryItem] 準備人工調整庫存: {}, 數量: {}, 原因: {}", command.productId(), command.quantity(), command.reason());
		if (this.stock + command.quantity() < 0) {
			log.error("[InventoryItem] 庫存不足以扣除: {}, 目前庫存: {}, 要求調整: {}", command.productId(), this.stock, command.quantity());
			throw new IllegalStateException("庫存不足以完成手動調整扣減");
		}
		AggregateLifecycle.apply(new StockAdjustedEvent(command.productId(), command.quantity(), command.reason()));
	}

	@EventSourcingHandler
	public void on(StockReducedEvent event) {
		this.stock -= event.quantity();
	}

	@EventSourcingHandler
	public void on(StockAddedEvent event) {
		this.stock += event.quantity();
	}

	@EventSourcingHandler
	public void on(StockAdjustedEvent event) {
		this.stock += event.quantity();
	}

	@CommandHandler
	public void handle(ReserveStockCommand command) {
		log.info("[InventoryItem] 準備鎖定庫存: {}, 訂單: {}, 數量: {}", command.productId(), command.orderId(), command.quantity());
		
		int currentReserved = reservations.values().stream().mapToInt(Integer::intValue).sum();
		if (this.stock - currentReserved < command.quantity()) {
			log.error("[InventoryItem] 庫存不足以鎖定: {}, 目前庫存: {}, 已鎖定: {}, 要求鎖定: {}", 
				command.productId(), this.stock, currentReserved, command.quantity());
			throw new IllegalStateException("庫存不足以完成鎖定");
		}
		
		AggregateLifecycle.apply(new StockReservedEvent(command.productId(), command.orderId(), command.quantity()));
	}

	@CommandHandler
	public void handle(ConfirmStockReservationCommand command) {
		log.info("[InventoryItem] 確認扣減已鎖定庫存: {}, 訂單: {}", command.productId(), command.orderId());
		if (!reservations.containsKey(command.orderId())) {
			throw new IllegalStateException("找不到該訂單的庫存鎖定紀錄");
		}
		AggregateLifecycle.apply(new StockReservationConfirmedEvent(command.productId(), command.orderId()));
	}

	@CommandHandler
	public void handle(CancelStockReservationCommand command) {
		log.info("[InventoryItem] 取消鎖定庫存: {}, 訂單: {}", command.productId(), command.orderId());
		if (!reservations.containsKey(command.orderId())) {
			log.warn("[InventoryItem] 找不到該訂單的庫存鎖定紀錄，可能已被處理或從未鎖定成功: {}", command.orderId());
			return; // 冪等處理
		}
		AggregateLifecycle.apply(new StockReservationCancelledEvent(command.productId(), command.orderId(), command.quantity()));
	}

	@EventSourcingHandler
	public void on(StockReservedEvent event) {
		this.reservations.put(event.orderId(), event.quantity());
	}

	@EventSourcingHandler
	public void on(StockReservationConfirmedEvent event) {
		Integer reservedQty = this.reservations.remove(event.orderId());
		if (reservedQty != null) {
			this.stock -= reservedQty;
		}
	}

	@EventSourcingHandler
	public void on(StockReservationCancelledEvent event) {
		this.reservations.remove(event.orderId());
	}

	// 供初始化的方法，讓外部可以直接設定初始庫存 (需配合 Command 建立)
	// 我們會建立一個 CreateInventoryItemCommand 以應對。
}
