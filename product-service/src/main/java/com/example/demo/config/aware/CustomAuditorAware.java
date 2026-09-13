package com.example.demo.config.aware;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class CustomAuditorAware implements AuditorAware<String> {

	/**
	 * 取得當前操作者，提供給自動審計欄位使用。
	 * 
	 * @return 包含當前操作者的 Optional 物件，如果沒有操作者則預設為 "SYSTEM"
	 */
	@Override
	public Optional<String> getCurrentAuditor() {
		String currentUserAccount = "SYSTEM"; // 暫時寫死，後續可整合 Spring Security 取得當前使用者
		return Optional.of(StringUtils.isNotBlank(currentUserAccount) ? currentUserAccount : "SYSTEM");
	}

}
