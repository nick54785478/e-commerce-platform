package com.omni.inventory.api.event;

/**
 * InventoryItemCreatedEvent - 庫存項目已建立事件
 */
public record InventoryItemCreatedEvent(String productId, Integer initialStock) {
}
