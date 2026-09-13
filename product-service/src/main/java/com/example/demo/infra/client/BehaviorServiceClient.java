package com.example.demo.infra.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

/**
 * Spring 6 HTTP Interface client for the Behavior Service.
 */
@HttpExchange("/api/v1/behaviors")
public interface BehaviorServiceClient {

    @PostExchange("/log")
    void logBehavior(@RequestBody Map<String, Object> payload);
}
