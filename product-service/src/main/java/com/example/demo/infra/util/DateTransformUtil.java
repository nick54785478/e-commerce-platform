package com.example.demo.infra.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * DateTransformUtil - 日期轉換工具
 * <p>
 * 提供各種日期格式字串與 Date, LocalDate, LocalDateTime 之間的轉換邏輯。
 * </p>
 */
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTransformUtil {

	/**
	 * 將 LocalDate 轉為字串
	 * 
	 * @param localDate 日期
	 * @return 格式化後的字串 (yyyy-MM-dd)
	 */
	public static String transformLocalDateToString(LocalDate localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		if (Objects.isNull(localDate)) {
			return null;
		}
		return localDate.format(formatter);
	}

	/**
	 * 將字串轉為 LocalDate
	 * 
	 * @param localDate 日期字串 (yyyy-MM-dd)
	 * @return LocalDate
	 */
	public static LocalDate transformStringToLocalDate(String localDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(localDate, formatter);
	}

	/**
	 * 將字串轉為 Date
	 * 
	 * @param pattern 日期格式
	 * @param date    日期字串
	 * @return Date
	 */
	public static Date parse(String pattern, String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		if (StringUtils.isBlank(date)) {
			return null;
		}

		return transformLocalDateTimeToDate(LocalDateTime.parse(date, formatter));
	}

	/**
	 * 將 Date 轉為字串
	 * 
	 * @param pattern 日期格式
	 * @param date    Date
	 * @return 格式化後的字串
	 */
	public static String format(String pattern, Date date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		if (Objects.isNull(date)) {
			return null;
		}
		LocalDateTime localDateTime = transformDateToLocalDateTime(date);
		return localDateTime.format(formatter);
	}

	/**
	 * 將字串轉為 LocalDateTime
	 */
	public static LocalDateTime transformStringToLocalDateTime(String pattern, String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDateTime.parse(date, formatter);
	}

	/**
	 * 將 LocalDateTime 轉為 Date
	 */
	private static Date transformLocalDateTimeToDate(LocalDateTime date) {
		if (Objects.isNull(date)) {
			return null;
		}
		return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 將 Date 轉為 LocalDateTime
	 */
	private static LocalDateTime transformDateToLocalDateTime(Date date) {
		if (Objects.isNull(date)) {
			return null;
		}
		Instant instant = date.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

}
