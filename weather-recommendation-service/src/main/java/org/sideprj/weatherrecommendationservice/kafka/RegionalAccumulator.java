package org.sideprj.weatherrecommendationservice.kafka;

import java.util.Map;

import org.sideprj.openweathermicroservices.avro.Severity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionalAccumulator {

    private long count;

    private double tempSum;

    private Map<Severity, Long> severityCount;

    public void increaseCount() {
        count++;
    }

    public void addTemp(double temp) {
        tempSum += temp;
    }

    public void countSeverity(Severity severity) {
        severityCount.merge(severity, 1L, Long::sum);
    }
}
