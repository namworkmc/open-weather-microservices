package org.sideprj.weatheranalyticsservice.repository;

import org.sideprj.weatheranalyticsservice.model.entity.HotWeatherAlertEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotWeatherAlertRepository extends MongoRepository<HotWeatherAlertEntity, String> {
}
