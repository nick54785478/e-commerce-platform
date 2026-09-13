package com.example.demo.config.aware;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class CustomAuditorAware implements AuditorAware<String> {

	/**
	 * 取得當前的使用者帳號，作為稽核欄位的值。
	 * 
	 * @return 當前使用者帳號的 Optional 實例，若無當前使用者帳號為空，則回傳 SYSTEM。
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		String currentUserAccount = "SYSTEM"; // 從上下文中取得 使用者帳號
		return Optional.of(StringUtils.isNotBlank(currentUserAccount) ? currentUserAccount : "SYSTEM");
	}

}
