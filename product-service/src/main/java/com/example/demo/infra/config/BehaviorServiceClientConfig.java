package com.example.demo.infra.config;

import com.example.demo.infra.client.BehaviorServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class BehaviorServiceClientConfig {

    @Value("${omni.behavior-service.url:http://localhost:8084}")
    private String behaviorServiceUrl;

    @Bean
    public BehaviorServiceClient behaviorServiceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(behaviorServiceUrl)
                .build();
                
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(BehaviorServiceClient.class);
    }
}
