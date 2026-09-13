package com.omni.payment.infra.projection.payment;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PaymentView - 支付讀取模型 (Read Model)
 * <p>
 * 此實體屬於 Infrastructure Layer，是資料庫中支付視圖的表示。
 * 採用充血模型 (Rich Model) 設計，將狀態變更與判斷邏輯封裝在實體內部，避免貧血 Service 症候群。
 * </p>
 * <h3>設計細節：</h3>
 * <ul>
 * <li><b>狀態保護：</b> markXXX 方法不建議加防呆檢查，確保事件重播 (Replay) 時不會因為異常而中斷更新。</li>
 * <li><b>業務輔助：</b> 提供 canXXX 方法，方便查詢端判斷該支付目前的後續可用操作。</li>
 * </ul>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_view") // 建議明確指定資料表名稱
public class PaymentView {

	/**
	 * 支付唯一識別碼 (對應 Aggregate 的 PaymentId)
	 */
	@Id
	private String paymentId;

	/**
	 * 關聯的訂單 ID
	 */
	private String orderId;

	/**
	 * 支付金額
	 */
	private BigDecimal amount;

	/**
	 * 支付狀態
	 * <p>
	 * 狀態流轉：CREATED -> PROCESSED -> REFUNDED (或 CREATED -> CANCELLED)
	 * </p>
	 */
	private String status;

	// ##### 1. 狀態變更方法 (State Mutators) #####

	/**
	 * 標記付款已建立
	 * <p>
	 * 接收到 PaymentCreatedEvent 時呼叫，初始化支付紀錄。
	 * </p>
	 */
	public void markCreated() {
		this.status = "CREATED";
	}

	/**
	 * 標記付款已處理 (已扣款)
	 * <p>
	 * 扣款完成後呼叫。內建冪等性檢查以防重複事件。
	 * </p>
	 */
	public void markProcessed() {
		if ("PROCESSED".equals(this.status)) {
			return; // 冪等控制：若已處理則直接忽略
		}
		this.status = "PROCESSED";
	}

	/**
	 * 標記付款已取消
	 * <p>
	 * 訂單失敗，尚未扣款就被取消後呼叫。
	 * </p>
	 */
	public void markCancelled() {
		this.status = "CANCELLED";
	}

	/**
	 * 標記付款已退款
	 * <p>
	 * 觸發退款，後續出貨失敗或退貨時，標記該筆支付已完成退款程序。
	 * </p>
	 */
	public void markRefunded() {
		this.status = "REFUNDED";
	}

	// ##### 2. 業務判斷方法 (Business Predicates) #####

	/**
	 * 判斷是否可以執行支付扣款
	 * 
	 * @return true 代表狀態為已建立，尚未扣款
	 */
	public boolean canBeProcessed() {
		return "CREATED".equals(this.status);
	}

	/**
	 * 判斷是否可以執行取消
	 * 
	 * @return true 代表尚未進入終端狀態 (已取消/已退款)
	 */
	public boolean canBeCancelled() {
		return !("CANCELLED".equals(this.status) || "REFUNDED".equals(this.status));
	}

	/**
	 * 判斷是否可以執行退款
	 * 
	 * @return true 代表在已扣款 (PROCESSED) 情況下才允許退款
	 */
	public boolean canBeRefunded() {
		return "PROCESSED".equals(this.status);
	}
}
