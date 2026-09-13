package com.example.demo.infra.adapter;

import com.example.demo.application.port.out.InventoryServicePort;
import com.omni.inventory.api.command.AdjustStockCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
class InventoryServiceAdapter implements InventoryServicePort {

    private final CommandGateway commandGateway;

    @Override
    public CompletableFuture<Void> adjustStock(AdjustStockCommand command) {
        return commandGateway.send(command);
    }
}
