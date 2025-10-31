package org.sideprj.weatheranalyticsservice.mapper;

import org.sideprj.openweathermicroservices.avro.HotWeatherAlertEvent;
import org.sideprj.openweathermicroservices.avro.WeatherEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public abstract class HotWeatherEventMapperDecorator implements HotWeatherEventMapper {

    private final HotWeatherEventMapper delegate;

    protected HotWeatherEventMapperDecorator(@Qualifier("delegate") HotWeatherEventMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public HotWeatherAlertEvent toHotWeatherAlertEvent(WeatherEvent weatherEvent) {
        return delegate.toHotWeatherAlertEvent(weatherEvent);
    }
}
