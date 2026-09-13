package com.omni.inventory.config;

import javax.sql.DataSource;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.omni.inventory.infra.persistence.InventoryViewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * StartupInventoryReplayService - 啟動時自動重播歷史事件
 *
 * <p>
 * 【改良版】當 {@code inventory_view} 為空時，在 Axon Processors 啟動之前，
 * 直接透過 JDBC 刪除 {@code token_entry} 裡的 Token 紀錄。
 * 這樣 Axon 在啟動 TrackingEventProcessor 時會找不到 Token，
 * 自動從第 0 個事件開始消費，無需呼叫任何 Axon API，避免執行緒死鎖。
 * </p>
 *
 * <h3>為何用 ApplicationStartedEvent 而不是 ApplicationReadyEvent？</h3>
 * <p>
 * {@code ApplicationStartedEvent} 在 Spring 容器初始化完成後、
 * Axon Processors 正式開始追蹤之前觸發，因此可以安全地在此時刪除 Token。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartupInventoryReplayService implements ApplicationListener<ApplicationStartedEvent> {

	private static final String DELETE_TOKENS_SQL =
			"DELETE FROM token_entry WHERE processor_name IN " +
			"('inventory-event-handler', 'inventory-projector', 'com.omni.inventory.application.service')";

	private final InventoryViewRepository inventoryViewRepository;
	private final DataSource dataSource;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		long inventoryCount;
		try {
			inventoryCount = inventoryViewRepository.count();
		} catch (Exception e) {
			log.warn("[StartupReplay] 無法查詢 inventory_view（可能是首次啟動，資料表尚未建立），跳過 Replay 檢查。");
			return;
		}

		log.info("[StartupReplay] inventory_view 目前資料筆數: {}", inventoryCount);

		if (inventoryCount == 0) {
			log.warn("[StartupReplay] inventory_view 為空，清除 token_entry 讓 Axon Processor 從頭重播...");
			try {
				JdbcTemplate jdbc = new JdbcTemplate(dataSource);
				int deleted = jdbc.update(DELETE_TOKENS_SQL);
				log.info("[StartupReplay] ✅ 已清除 {} 筆 token_entry，Axon 將從第 0 個事件重新開始消費。", deleted);
			} catch (Exception e) {
				log.error("[StartupReplay] ❌ 清除 token 失敗: {}，請手動呼叫 POST /inventory/init", e.getMessage(), e);
			}
		} else {
			log.info("[StartupReplay] inventory_view 已有 {} 筆資料，跳過 Replay。", inventoryCount);
		}
	}
}
