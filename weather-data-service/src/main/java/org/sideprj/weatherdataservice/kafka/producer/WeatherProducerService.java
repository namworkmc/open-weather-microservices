package org.sideprj.weatherdataservice.kafka.producer;


import org.sideprj.openweathermicroservices.avro.WeatherEvent;

public interface WeatherProducerService {
    void produce(String city, WeatherEvent weatherEvent);
}
