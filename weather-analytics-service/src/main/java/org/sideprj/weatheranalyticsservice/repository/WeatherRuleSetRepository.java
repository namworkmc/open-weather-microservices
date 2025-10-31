package org.sideprj.weatheranalyticsservice.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.sideprj.weatheranalyticsservice.model.entity.WeatherRuleSetEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRuleSetRepository extends MongoRepository<WeatherRuleSetEntity, String> {

    Optional<WeatherRuleSetEntity> findByRegionIgnoreCase(String region);

    List<WeatherRuleSetEntity> findByRegionIn(Collection<String> regions);
}
