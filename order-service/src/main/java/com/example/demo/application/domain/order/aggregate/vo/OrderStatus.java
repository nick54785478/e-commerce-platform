package com.example.demo.application.domain.order.aggregate.vo;

public enum OrderStatus {
	CREATED, // 訂單已建立
	NOTIFIED, // 已通知出貨 (等待物流處理)
	SHIPPED, // 已出貨 (人工確認)
	RETURNED, // 退貨
	CANCELLED // 已取消
}
