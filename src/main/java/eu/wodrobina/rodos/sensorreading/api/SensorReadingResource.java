package eu.wodrobina.rodos.sensorreading.api;

import java.math.BigDecimal;
import java.time.Instant;

public record SensorReadingResource(
        Long id,
        String sensorName,
        BigDecimal reading,
        String unit,
        Instant createdAt) {
}
