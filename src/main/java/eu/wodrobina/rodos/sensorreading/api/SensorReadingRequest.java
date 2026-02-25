package eu.wodrobina.rodos.sensorreading.api;

import eu.wodrobina.rodos.sensorreading.SensorUnit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SensorReadingRequest(UUID sensorId,
                                   BigDecimal reading, String unit, Instant createdAt) {

    public static SensorReadingRequest of(UUID sensorId, BigDecimal reading, SensorUnit unit) {
        return new SensorReadingRequest(sensorId, reading, unit.getUnit(), Instant.now());
    }
}
