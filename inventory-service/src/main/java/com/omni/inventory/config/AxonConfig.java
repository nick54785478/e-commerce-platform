package com.omni.inventory.config;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * AxonConfig - 設定 Axon 事件處理器行為與序列化設定
 */
@Configuration
public class AxonConfig {

	/**
	 * 設定 inventory-event-handler Processor 使用 TAIL 作為初始 Token
	 */
	@Autowired
	void configureInventoryEventHandlerProcessor(EventProcessingConfigurer configurer) {
		configurer.registerTrackingEventProcessorConfiguration(
				"inventory-event-handler",
				config -> TrackingEventProcessorConfiguration
						.forSingleThreadedProcessing()
						.andInitialTrackingToken(streamableMessageSource -> streamableMessageSource.createTailToken())
		);
	}

	/**
	 * 設定 JacksonSerializer 以匹配 product-service 發出的事件格式。
	 * product-service 啟用了 DefaultTyping (WRAPPER_ARRAY)，如果不加上這段設定，
	 * inventory-service 在反序列化時會因為不認識型別陣列 (如 ["java.math.BigDecimal",18500]) 而失敗並跳過事件。
	 */
	@Bean
	@Primary
	public Serializer jacksonSerializer(ObjectMapper objectMapper) {
		ObjectMapper axonMapper = objectMapper.copy();

		axonMapper.registerModule(new JavaTimeModule());
		axonMapper.registerModule(new ParameterNamesModule());

		axonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		axonMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		axonMapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY);

		axonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		axonMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.WRAPPER_ARRAY);

		return JacksonSerializer.builder().objectMapper(axonMapper).build();
	}
}
