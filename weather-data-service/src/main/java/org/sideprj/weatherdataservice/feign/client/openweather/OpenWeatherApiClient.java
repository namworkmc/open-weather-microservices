package org.sideprj.weatherdataservice.feign.client.openweather;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "OpenWeatherApiClient", url = "${openweather.api.url}", path = "/data/2.5")
public interface OpenWeatherApiClient {

    @GetMapping("/weather?q={city}")
    OpenWeatherResponse getWeatherByCity(@PathVariable("city") String city);
}
