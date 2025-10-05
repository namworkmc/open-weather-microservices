package org.sideprj.weatherdataservice.feign.client.openweather.impl;

import org.sideprj.weatherdataservice.feign.client.openweather.Model200;
import org.sideprj.weatherdataservice.feign.client.openweather.OpenWeatherApiClient;
import org.sideprj.weatherdataservice.feign.client.openweather.OpenWeatherService;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenWeatherServiceImpl implements OpenWeatherService {

    private final OpenWeatherApiClient openWeatherApiClient;

    @Override
    @CircuitBreaker(name = "openWeatherApiClient")
    @RateLimiter(name = "openWeatherApiClient")
    @Retry(name = "openWeatherApiClient")
    public Model200 getWeatherByCity(String city) {
        return openWeatherApiClient.getWeatherByCity(city);
    }
}
