package org.sideprj.weatherdataservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CacheService {

    Optional<LocalDateTime> getLastFetchedTime(String city);

    LocalDateTime putLastFetchedTime(String city, LocalDateTime lastFetchedTime);
}
