package com.omni.inventory.infra.processor;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.omni.inventory.api.event.InventoryItemCreatedEvent;
import com.omni.inventory.api.event.StockAddedEvent;
import com.omni.inventory.api.event.StockReducedEvent;
import com.omni.inventory.api.event.StockAdjustedEvent;
import com.omni.inventory.infra.persistence.InventoryViewRepository;
import com.omni.inventory.infra.projection.InventoryView;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
@ProcessingGroup("inventory-projector")
public class InventoryProjector {

	private final InventoryViewRepository repository;

	@EventHandler
	public void on(InventoryItemCreatedEvent event) {
		String originalProductId = event.productId().replace("INV-", "");
		InventoryView view = new InventoryView(originalProductId, event.initialStock());
		repository.save(view);
	}

	@EventHandler
	public void on(StockReducedEvent event) {
		String originalProductId = event.productId().replace("INV-", "");
		repository.findById(originalProductId).ifPresent(view -> {
			view.setStock(view.getStock() - event.quantity());
			repository.save(view);
		});
	}

	@EventHandler
	public void on(StockAddedEvent event) {
		String originalProductId = event.productId().replace("INV-", "");
		repository.findById(originalProductId).ifPresent(view -> {
			view.setStock(view.getStock() + event.quantity());
			repository.save(view);
		});
	}

	@EventHandler
	public void on(StockAdjustedEvent event) {
		String originalProductId = event.productId().replace("INV-", "");
		repository.findById(originalProductId).ifPresent(view -> {
			view.setStock(view.getStock() + event.quantity());
			repository.save(view);
		});
	}
}
