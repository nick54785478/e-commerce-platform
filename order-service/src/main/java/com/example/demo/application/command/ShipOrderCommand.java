package com.example.demo.application.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * ShipOrderCommand - 系統內部出貨指令
 */
public record ShipOrderCommand(@TargetAggregateIdentifier String orderId) {
}
