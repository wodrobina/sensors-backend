package eu.wodrobina.rodos.sensorreading.api;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Sensor reading representation.
 *
 * @param id
 * @param sensorName
 * @param reading
 * @param unit
 * @param createdAt
 */
public record SensorReadingResource(
        Long id,
        String sensorName,
        BigDecimal reading,
        String unit,
        Instant createdAt) {
}
