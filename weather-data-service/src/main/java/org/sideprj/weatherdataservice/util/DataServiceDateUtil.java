package org.sideprj.weatherdataservice.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataServiceDateUtil {

    public boolean isEqualOrAfter(LocalDateTime localDateTime, LocalDateTime otherTimestamp) {
        return localDateTime.isEqual(otherTimestamp) || localDateTime.isAfter(otherTimestamp);
    }

    public LocalDateTime toLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
