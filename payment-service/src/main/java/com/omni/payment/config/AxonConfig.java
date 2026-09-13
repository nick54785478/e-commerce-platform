package com.omni.payment.config;

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
public class AxonConfig {

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
