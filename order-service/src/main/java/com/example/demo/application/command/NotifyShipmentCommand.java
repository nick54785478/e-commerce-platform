package com.example.demo.application.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * NotifyShipmentCommand - 通知出貨指令
 * <p>由 Saga 觸發：通知 Aggregate 付款已成功可以準備出貨了</p>
 */
public record NotifyShipmentCommand(@TargetAggregateIdentifier String orderId) {}
