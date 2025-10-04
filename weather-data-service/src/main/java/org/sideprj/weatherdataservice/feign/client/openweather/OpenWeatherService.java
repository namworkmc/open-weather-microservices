package org.sideprj.weatherdataservice.feign.client.openweather;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

public interface OpenWeatherService {
    @CircuitBreaker(name = "openWeatherApiClient", fallbackMethod = "getWeatherByCityFallback")
    Model200 getWeatherByCity(String city);
}
