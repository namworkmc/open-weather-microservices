package org.sideprj.weatheranalyticsservice.controller;

import java.util.List;

import org.sideprj.weatheranalyticsservice.mapper.WeatherRuleSetMapper;
import org.sideprj.weatheranalyticsservice.model.dto.WeatherRuleSetDto;
import org.sideprj.weatheranalyticsservice.service.WeatherRuleSetService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/weather-rule-sets")
@RequiredArgsConstructor
public class WeatherRuleSetController {

    private final WeatherRuleSetMapper weatherRuleSetMapper;

    private final WeatherRuleSetService weatherRuleSetService;

    @PostMapping
    public List<WeatherRuleSetDto> saveWeatherRuleSets(@RequestBody List<@Valid WeatherRuleSetDto> weatherRuleSetDtos) {
        var entities = weatherRuleSetMapper.toEntities(weatherRuleSetDtos);
        return weatherRuleSetMapper.toDtos(weatherRuleSetService.save(entities));
    }
}
