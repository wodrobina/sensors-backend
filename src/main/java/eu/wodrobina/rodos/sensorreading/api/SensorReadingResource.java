package eu.wodrobina.rodos.sensorreading.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Sensor reading representation.
 *
 * @param id
 * @param sensorId
 * @param reading
 * @param unit
 * @param createdAt
 */
public record SensorReadingResource(
        Long id,
        UUID sensorId,
        BigDecimal reading,
        String unit,
        Instant createdAt) {
}
