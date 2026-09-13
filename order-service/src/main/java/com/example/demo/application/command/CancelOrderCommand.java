package com.example.demo.application.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.modelling.command.TargetAggregateVersion;

/**
 * CancelOrderCommand - 取消訂單指令
 * 
 * @param orderId 訂單 ID
 * @param version 樂觀鎖版本 (傳入 null 則系統強制執行，不檢查版本)
 */
public record CancelOrderCommand(@TargetAggregateIdentifier String orderId, @TargetAggregateVersion Long version) {
	// 增加一個方便的建構子，供 Saga 等內部系統使用
	public CancelOrderCommand(String orderId) {
		this(orderId, null);
	}
}
