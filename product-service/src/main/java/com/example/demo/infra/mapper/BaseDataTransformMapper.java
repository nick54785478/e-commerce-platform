package com.example.demo.infra.mapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.example.demo.infra.util.DateTransformUtil;
import com.example.demo.infra.util.JsonParseUtil;
/**
 * BaseDataTransformMapper - 基礎資料型態轉換器
 * <p>
 * 提供 MapStruct 映射時共用的自訂轉換邏輯 (如：日期格式轉換、字串與列表互轉、JSON 序列化)。
 * </p>
 */
@Mapper(componentModel = "spring")
public interface BaseDataTransformMapper {

	/**
	 * 將字串解析為 Date
	 */
	@Named("parseStringToDate")
	public default Date parseStringToDate(String dateStr) {
		return DateTransformUtil.parse("yyyy-MM-dd", dateStr);
	}

	/**
	 * 將 Date 格式化為字串
	 */
	@Named("formatDateToString")
	public default String formatDateToString(Date date) {
		return DateTransformUtil.format("yyyy-MM-dd", date);
	}

	/**
	 * 將 LocalDate 格式化為字串
	 */
	@Named("formatLocalDateToString")
	public default String formatLocalDateToString(LocalDate date) {
		return DateTransformUtil.transformLocalDateToString(date);
	}

	/**
	 * 將 List<String> 轉換為逗號分隔的字串
	 */
	@Named("transformListToString")
	public default String transformListToString(List<String> target) {
		return String.join(",", target);
	}

	/**
	 * 將逗號分隔的字串轉換為 List<String>
	 */
	@Named("transformStringToList")
	public default List<String> transformStringToList(String target) {
		return Arrays.stream(target.split(",")).map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
	}
	
	/**
	 * 將物件序列化為 JSON 字串
	 */
	@Named("serializeObjectToJson")
	public default String serializeObjectToJson(Object target) {
		return JsonParseUtil.serialize(target);
	}
}
