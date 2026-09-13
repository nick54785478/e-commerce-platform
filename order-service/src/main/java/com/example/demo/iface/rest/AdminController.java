package com.example.demo.iface.rest;

import java.util.concurrent.CompletableFuture;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final EventProcessingConfiguration eventProcessingConfiguration;

	/**
	 * API: 重置追蹤事件處理器 (Replay Events)
	 * <p>
	 * 例如: POST /admin/replay/order-group 將會重新從頭消費所有 Order 相關事件來重建 Read Model (View)。
	 * </p>
	 */
	@PostMapping("/replay/{processingGroup}")
	public CompletableFuture<ResponseEntity<String>> replayEvents(@PathVariable String processingGroup) {

		return CompletableFuture.supplyAsync(() -> {
			eventProcessingConfiguration.eventProcessor(processingGroup, TrackingEventProcessor.class)
					.ifPresentOrElse(processor -> {
						log.warn("[Admin] 準備重置並重播事件群組: {}", processingGroup);
						processor.shutDown();
						processor.resetTokens();
						processor.start();
						log.info("[Admin] 事件群組: {} 重播程序啟動完成", processingGroup);
					}, () -> {
						log.error("[Admin] 找不到指定的事件處理群組: {}", processingGroup);
						throw new IllegalArgumentException("Processor not found");
					});

			return ResponseEntity.ok("事件重播指令已下達: " + processingGroup);
		});
	}
}
