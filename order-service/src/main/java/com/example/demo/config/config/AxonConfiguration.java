package com.example.demo.config.config;

import org.axonframework.config.ConfigurationScopeAwareProvider;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@Configuration
public class AxonConfiguration {

	@Bean
	@Primary
	public Serializer jacksonSerializer(ObjectMapper objectMapper) {
		ObjectMapper axonMapper = objectMapper.copy();

		// 1. 註冊時間模組
		axonMapper.registerModule(new JavaTimeModule());
		axonMapper.registerModule(new ParameterNamesModule());

		// 2. 忽略未知屬性，避免未來的事件包含新屬性時解析失敗
		axonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 3. 處理任意屬性存取，以支援 Java Record
		axonMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		axonMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

		// 4. 禁用時間戳記格式
		axonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// 5. 設定型別資訊
		axonMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.WRAPPER_ARRAY);

		return JacksonSerializer.builder().objectMapper(axonMapper).build();
	}

	// 定義訂單快照觸發器，這裡設定為每 50 個事件觸發一次快照
	@Bean
	public SnapshotTriggerDefinition orderSnapshotTriggerDefinition(Snapshotter snapshotter) {
		// 使用 EventCountSnapshotTriggerDefinition，這是最常見的基於事件數量的觸發策略
		return new EventCountSnapshotTriggerDefinition(snapshotter, 50);
	}

	/**
	 * 定義商品快照觸發器
	 * <p>
	 * 允許一個 Aggregate 的事件數量達到 100 筆時，自動產生一個快照並存入 Axon Server。
	 * </p>
	 */
	@Bean(name = "productSnapshotTriggerDefinition")
	public SnapshotTriggerDefinition productSnapshotTriggerDefinition(Snapshotter snapshotter) {
		// 閾值設定為 100 (可依業務需求調整)
		return new EventCountSnapshotTriggerDefinition(snapshotter, 100);
	}

	/**
	 * 配置 DeadlineManager
	 */
	@Bean
	public DeadlineManager deadlineManager(org.axonframework.common.transaction.TransactionManager transactionManager,
			org.axonframework.config.Configuration configuration) {
		// 使用 SimpleDeadlineManager，適用於單節點的排程需求
		return SimpleDeadlineManager.builder().scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
				.transactionManager(transactionManager).build();
	}
}
