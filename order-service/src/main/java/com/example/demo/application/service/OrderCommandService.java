package com.example.demo.application.service;

import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import com.example.demo.application.domain.order.aggregate.Order;
import com.example.demo.application.command.CancelOrderCommand;
import com.example.demo.application.command.ConfirmOrderShipmentCommand;
import com.example.demo.application.command.CreateOrderCommand;
import com.example.demo.application.command.ReturnOrderCommand;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OrderCommandService - 訂單命令應用服務
 *
 * <p>
 * 這是系統架構中的應用層服務（Unbound Adapter），負責接收外部（如 REST API）
 * 傳來的命令 (Commands)，並透過 {@link CommandGateway} 路由至對應的 {@link Order} 聚合根。
 * </p>
 *
 * <h3>主要職責：</h3>
 * <ul>
 * <li><b>命令轉發：</b> 將來自介面層的請求轉化為領域層可處理的命令。</li>
 * <li><b>樂觀鎖控制：</b> 透過帶有 {@code version} 的命令來進行狀態檢查與併發控制。</li>
 * <li><b>非同步響應：</b> 回傳 {@link CompletableFuture} 處理結果，讓呼叫端 API
 * 決定是要同步等待或非同步處理。</li>
 * </ul>
 */
@Slf4j
@Service
@AllArgsConstructor
public class OrderCommandService {

	private final CommandGateway commandGateway;

	/**
	 * 建立新訂單
	 * <p>
	 * 處理訂單建立的初始狀態。發送命令後，由 {@link Order} 聚合根負責驗證並拋出對應領域事件。
	 * 成功建立後會發布 {@code OrderCreatedEvent}，進而觸發 Saga 流程進行後續的庫存扣留與付款建立。
	 * </p>
	 *
	 * @param command 包含訂單項目、用戶資訊及總金額的建立命令
	 * @return {@link CompletableFuture} 包含建立後的訂單 ID (orderId)
	 */
	public CompletableFuture<String> createOrder(CreateOrderCommand command) {
		log.info("[Order Service] 執行訂單建立流程: orderId={}, 項目數量: {}", command.orderId(), command.items().size());

		// 透過 Gateway 發送命令，Aggregate 的建立函數將會被觸發
		return commandGateway.send(command);
	}

	/**
	 * 確認並更新訂單為已出貨
	 * <p>
	 * 當物流系統或管理員完成出貨後呼叫。<b>業務邏輯限制：</b> 訂單狀態必須處於已確認 (APPROVED) 或處理中，
	 * 且命令中的 {@code version} 必須與當前聚合根的訂單版本一致，以確保狀態更新的正確性。
	 * </p>
	 *
	 * @param command 出貨確認命令，包含訂單 ID 與目標版本號
	 * @return {@link CompletableFuture} 用於等待命令是否成功執行
	 */
	public CompletableFuture<Void> confirmShipment(ConfirmOrderShipmentCommand command) {
		log.info("[Order Service] 收到訂單出貨請求，目標訂單: {}, 版本: {}", command.orderId(), command.version());

		return commandGateway.send(command);
	}

	/**
	 * 取消/作廢訂單
	 * <p>
	 * 執行訂單取消流程。此命令可能來自用戶主動操作或系統逾時未付款的補償動作。<b>狀態機限制：</b>
	 * 若訂單已經進入出貨狀態 (SHIPPED)，則無法被取消，操作將會失敗並拋出異常。
	 * </p>
	 *
	 * @param command 包含訂單 ID 與目標版本號的取消命令
	 * @return {@link CompletableFuture} 處理結果
	 */
	public CompletableFuture<Void> cancelOrder(CancelOrderCommand command) {
		log.info("[Order Service] 執行訂單取消命令: {}, 版本: {}", command.orderId(), command.version());

		return commandGateway.send(command);
	}

	/**
	 * 申請訂單退貨
	 * <p>
	 * 針對已出貨 (SHIPPED) 的訂單進行退貨處理。成功執行後會發布 {@code OrderReturnedEvent}，並觸發 Saga
	 * 進行後續的退款與庫存歸還流程。
	 * </p>
	 *
	 * @param command 退貨命令，包含訂單 ID、目標版本號與退貨原因
	 * @return {@link CompletableFuture} 用於等待退貨請求是否被接受
	 */
	public CompletableFuture<Void> returnOrder(ReturnOrderCommand command) {
		log.info("[Order Service] 接收退貨申請: OrderId={}, 原因={}", command.orderId(), command.reason());

		return commandGateway.send(command);
	}
}
