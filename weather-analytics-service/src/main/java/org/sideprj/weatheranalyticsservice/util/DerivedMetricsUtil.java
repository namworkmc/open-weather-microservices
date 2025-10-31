package org.sideprj.weatheranalyticsservice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DerivedMetricsUtil {

    public double calculateHeatIndex(double temperature, double humidity) {
        return temperature + 0.33 * (humidity / 100 * 6.105 * Math.exp((17.27 * temperature) / (237.7 + temperature))) - 4.0;
    }

    public double calculateDewPoint(double temperature, double humidity) {
        double a = 17.27;
        double b = 237.7;
        double alpha = ((a * temperature) / (b + temperature)) + Math.log(humidity / 100);
        return (b * alpha) / (a - alpha);
    }
}
