package org.sideprj.weatherdataservice.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.sideprj.weatherdataservice.service.CacheService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private static final String WEATHER_FETCHED_AT_CACHE = "weatherFetchedAtCache";

    @Override
    @Cacheable(value = WEATHER_FETCHED_AT_CACHE, key = "'weather:lastFetched:' + #city")
    public Optional<LocalDateTime> getLastFetchedTime(String city) {
        // Empty because of putLastFetchedTime has never been called
        return Optional.empty();
    }

    @Override
    @CachePut(value = WEATHER_FETCHED_AT_CACHE, key = "'weather:lastFetched:' + #city")
    public LocalDateTime putLastFetchedTime(String city, LocalDateTime lastFetchedTime) {
        return lastFetchedTime;
    }
}
