package org.sideprj.weatheranalyticsservice.repository;

import java.util.List;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherEventEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherEventRepository extends MongoRepository<WeatherEventEntity, String> {

    List<WeatherEventEntity> findByCityOrderByCreatedAtDesc(String city, Limit limit);
}
