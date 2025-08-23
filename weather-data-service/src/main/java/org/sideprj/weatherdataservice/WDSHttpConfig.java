package org.sideprj.weatherdataservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class WDSHttpConfig {

    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> requestTemplate.query("appid", openWeatherApiKey);
    }
}
