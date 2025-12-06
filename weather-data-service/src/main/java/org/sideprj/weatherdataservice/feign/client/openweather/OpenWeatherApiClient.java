package org.sideprj.weatherdataservice.feign.client.openweather;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = OpenWeatherApiClient.NAME, url = "${openweather.api.url}", path = "/data/2.5")
public interface OpenWeatherApiClient {

    String NAME = "OpenWeatherApiClient";

    @GetMapping("/weather?q={city}&units=metric")
    @CircuitBreaker(name = NAME)
    @RateLimiter(name = NAME)
    @Retry(name = NAME)
    Model200 getWeatherByCity(@PathVariable("city") String city);
}
