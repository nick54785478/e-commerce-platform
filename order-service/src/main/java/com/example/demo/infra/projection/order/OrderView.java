package com.example.demo.infra.projection.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OrderView - 訂單查詢模型 (Read Model)
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_view")
public class OrderView {

	/**
	 * 訂單唯一識別碼 (對應 Aggregate 的 OrderId)
	 */
	@Id
	private String orderId;

	/**
	 * 樂觀鎖版本號 (權威來源：Event Store Sequence Number)
	 * <p>
	 * 讀取端不負責產生版本號，而是依賴領域事件的 {@code @SequenceNumber}。
	 * 前端發起「取消」或「退貨」時，需要帶上此版本號以確保並發修改的安全性。
	 * </p>
	 */
	private Long version;

	/**
	 * 訂單品項清單 (EAGER 載入)
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "order_view_items", joinColumns = @JoinColumn(name = "order_id"))
	private List<OrderItemView> items = new ArrayList<>();

	/**
	 * 訂單總金額
	 */
	private BigDecimal amount;

	/**
	 * 訂單當前狀態
	 * <p>
	 * 狀態轉換：CREATED -> NOTIFIED -> SHIPPED -> RETURNED (或 CREATED -> CANCELLED)
	 * </p>
	 */
	private String status;

	// ##### 1. 狀態變更方法 (State Mutators - 充血模型) #####

	public void markShipped() {
		this.status = "SHIPPED";
	}

	public void markNotified() {
		this.status = "NOTIFIED";
	}

	public void markCancelled() {
		this.status = "CANCELLED";
	}

	/**
	 * 標記訂單為已退貨(RETURNED)
	 * <p>
	 * 對應事件：OrderReturnedEvent
	 * </p>
	 */
	public void markReturned() {
		this.status = "RETURNED";
	}

	// ##### 2. 業務判斷輔助方法 (Business Predicates) #####

	/**
	 * 判斷是否可以確認出貨
	 */
	public boolean canShip() {
		return "NOTIFIED".equals(this.status);
	}

	/**
	 * 判斷是否可以執行取消
	 * <p>
	 * 業務規則：未出貨、未取消、未退貨的情況下可以取消。
	 * </p>
	 */
	public boolean canCancel() {
		return !"SHIPPED".equals(this.status) && !"CANCELLED".equals(this.status) && !"RETURNED".equals(this.status);
	}

	/**
	 * 判斷是否可以執行退貨
	 * <p>
	 * 業務規則：只有已出貨(SHIPPED) 的訂單可以申請退貨。
	 * </p>
	 */
	public boolean canReturn() {
		return "SHIPPED".equals(this.status);
	}
}
