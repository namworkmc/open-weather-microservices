package org.sideprj.weatherrecommendationservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor(@Value("${spring.ai.openai.embedding.api-key}") String apiKey) {
        return requestTemplate -> requestTemplate.header("Authorization", "Bearer " + apiKey);
    }
}
