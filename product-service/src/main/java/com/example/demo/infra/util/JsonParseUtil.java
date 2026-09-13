package com.example.demo.infra.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JsonParseUtil - JSON 解析工具
 * <p>
 * 提供物件與 JSON 字串之間的序列化與反序列化封裝。
 * </p>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonParseUtil {

	protected static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 序列化物件為 JSON 字串
	 * 
	 * @param target 目標物件
	 * @return 序列化後的 JSON 字串
	 */
	public static String serialize(Object target) {
		try {
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			return mapper.writeValueAsString(target);
		} catch (JsonProcessingException e) {
			log.error("Occurred JsonProcessing Exception", e);
			return "";
		}
	}

	/**
	 * 反序列化 JSON 字串為指定類別物件
	 * 
	 * @param target JSON 字串
	 * @param clazz  目標類別
	 * @param <T>    泛型型別
	 * @return 反序列化後的物件，若失敗則回傳 null
	 */
	public static <T> T unserialize(String target, Class<T> clazz) {
		try {
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			return mapper.readValue(target, clazz);
		} catch (JsonMappingException e) {
			log.error("Occurred JsonMapping Exception", e);
			return null;
		} catch (JsonProcessingException e) {
			log.error("Occurred JsonProcessing Exception", e);
			return null;
		}
	}

	/**
	 * 反序列化 JSON 字串為物件列表
	 * 
	 * @param target JSON 字串
	 * @param clazz  目標類別
	 * @param <T>    泛型型別
	 * @return 物件列表，若失敗則回傳空列表
	 */
	public static <T> List<T> unserializeArrayOfObject(String target, Class<T> clazz) {
		try {
			return mapper.readValue(target, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
		} catch (JsonProcessingException e) {
			log.error("Occurred JsonProcessing Exception", e);
			return new ArrayList<>();
		}
	}
}
