package com.example.demo.infra.adapter;

import com.example.demo.application.port.out.BehaviorServicePort;
import com.example.demo.infra.client.BehaviorServiceClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import lombok.extern.slf4j.Slf4j;

/**
 * Adapter that implements LogBehaviorPort and delegates to the HTTP client.
 */
@Slf4j
@Component
public class BehaviorServiceAdapter implements BehaviorServicePort {

    private final BehaviorServiceClient behaviorServiceClient;

    public BehaviorServiceAdapter(BehaviorServiceClient behaviorServiceClient) {
        this.behaviorServiceClient = behaviorServiceClient;
    }

    @Override
    public void logBehavior(String userId, String sessionId, String itemId, String behaviorType, String tenantId) {
        // Fallback for missing sessionId (behavior-service requires it)
        String finalSessionId;
        if (sessionId != null && !sessionId.isEmpty()) {
            finalSessionId = sessionId;
        } else if (userId != null && !userId.isEmpty()) {
            finalSessionId = "session-u-" + userId;
        } else {
            finalSessionId = UUID.randomUUID().toString();
        }
                                
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("tenantId", tenantId);
        requestPayload.put("userId", userId);
        requestPayload.put("sessionId", finalSessionId);
        requestPayload.put("itemId", itemId);
        requestPayload.put("behaviorType", behaviorType);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("source", "product-service");
        requestPayload.put("metadata", metadata);

        CompletableFuture.runAsync(() -> {
            try {
                behaviorServiceClient.logBehavior(requestPayload);
                log.debug("Successfully logged behavior: {} for item: {}", behaviorType, itemId);
            } catch (Exception e) {
                log.error("Failed to log behavior to behavior-service: {}", e.getMessage());
            }
        });
    }
}
