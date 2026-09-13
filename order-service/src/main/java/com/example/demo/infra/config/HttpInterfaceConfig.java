package com.example.demo.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.demo.infra.client.ProductApiClient;

/**
 * HttpInterfaceConfig - 設定與註冊 Spring 6 HttpInterface
 */
@Configuration
public class HttpInterfaceConfig {

	@Bean
	public ProductApiClient productApiClient() {
		RestClient restClient = RestClient.builder()
				.baseUrl("http://localhost:8083")
				.defaultHeader("X-Tenant-ID", "TTRAVEL") // 配合前端預設的 Tenant ID
				.build();

		// 將 RestClient 轉換為 Adapter
		RestClientAdapter adapter = RestClientAdapter.create(restClient);

		// 透過 Factory 建立代理物件
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

		return factory.createClient(ProductApiClient.class);
	}
}
