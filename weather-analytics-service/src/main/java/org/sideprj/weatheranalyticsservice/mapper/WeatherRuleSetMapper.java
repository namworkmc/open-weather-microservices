package org.sideprj.weatheranalyticsservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.sideprj.weatheranalyticsservice.model.dto.WeatherRuleSetDto;
import org.sideprj.weatheranalyticsservice.model.entity.WeatherRuleSetEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WeatherRuleSetMapper {

    List<WeatherRuleSetEntity> toEntities(List<WeatherRuleSetDto> dtoList);

    void toEntity(WeatherRuleSetDto source, @MappingTarget WeatherRuleSetEntity target);

    List<WeatherRuleSetDto> toDtos(List<WeatherRuleSetEntity> entityList);
}
