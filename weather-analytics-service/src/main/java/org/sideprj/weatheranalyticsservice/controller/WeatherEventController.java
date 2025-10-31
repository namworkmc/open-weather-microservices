package org.sideprj.weatheranalyticsservice.controller;

import org.sideprj.weatheranalyticsservice.service.WeatherEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/weather-events")
@RequiredArgsConstructor
public class WeatherEventController {

    private final WeatherEventService weatherEventService;

    @GetMapping
    public void saveWeatherEvent() {
        weatherEventService.getDeviation("Hanoi", 123);
    }
}
