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

		// 2. 忽略未知的屬性 (防止舊版事件缺少欄位時反序列化失敗，如 ReplayToken)
		axonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 3. 允許反序列化沒有無參數建構子的 Java Record
		axonMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		axonMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

		// 4. 關閉將日期序列化為時間戳
		axonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// 5. 啟用預設的型別資訊
		axonMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.WRAPPER_ARRAY);

		return JacksonSerializer.builder().objectMapper(axonMapper).build();
	}

	// 設定訂單的快照觸發條件，每滿 50 個事件就建立一次快照
	@Bean
	public SnapshotTriggerDefinition orderSnapshotTriggerDefinition(Snapshotter snapshotter) {
		// 透過 EventCountSnapshotTriggerDefinition，當事件數量達到設定值時觸發快照建立
		return new EventCountSnapshotTriggerDefinition(snapshotter, 50);
	}

	/**
	 * 設定商品的快照觸發條件
	 * <p>
	 * 針對 Product Aggregate，每滿 100 個事件就建立一次快照，以提升加載效能並節省 Axon Server 資源。
	 * </p>
	 */
	@Bean(name = "productSnapshotTriggerDefinition")
	public SnapshotTriggerDefinition productSnapshotTriggerDefinition(Snapshotter snapshotter) {
		// 將閾值設為 100 (每 100 個事件拍一次快照)
		return new EventCountSnapshotTriggerDefinition(snapshotter, 100);
	}

	/**
	 * 註冊 DeadlineManager
	 */
	@Bean
	public DeadlineManager deadlineManager(org.axonframework.common.transaction.TransactionManager transactionManager,
			org.axonframework.config.Configuration configuration) {
		// 使用 SimpleDeadlineManager，適用於單節點或測試環境
		return SimpleDeadlineManager.builder().scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
				.transactionManager(transactionManager).build();
	}
}
