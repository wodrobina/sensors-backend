package eu.wodrobina.rodos.sensorreading;

import eu.wodrobina.rodos.sensorreading.api.SensorReadingRequest;
import eu.wodrobina.rodos.sensorreading.api.SensorReadingResource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * An entity that represents single sensor reading.
 */
class SensorReading {

    private Long id;
    private UUID sensorId;
    private BigDecimal reading;
    private String unit;
    private Instant createdAt;

    SensorReading(Long id, UUID sensorId, BigDecimal reading, String unit, Instant createdAt) {
        this.id = id;
        this.sensorId = sensorId;
        this.reading = reading.setScale(2, RoundingMode.HALF_UP);
        this.unit = unit;
        this.createdAt = createdAt;
    }

    public static SensorReading of(UUID sensorId,
                                   BigDecimal reading,
                                   SensorUnit sensorUnit) {
        return new SensorReading(null, sensorId, reading, sensorUnit.getUnit(), Instant.now());
    }

    public static SensorReading of(UUID sensorId,
                                   double reading,
                                   SensorUnit sensorUnit) {
        return new SensorReading(null, sensorId, BigDecimal.valueOf(reading), sensorUnit.getUnit(), Instant.now());
    }

    public static SensorReading from(SensorReadingRequest request) {
        return new SensorReading(null, request.sensorId(), request.reading(), request.unit(), Instant.now());
    }

    public Long id() {
        return id;
    }

    public UUID sensorId() {
        return sensorId;
    }

    public BigDecimal reading() {
        return reading;
    }

    public String unit() {
        return unit;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        SensorReading that = (SensorReading) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    SensorReadingResource asResource() {
        return toResource(this);
    }

    static SensorReadingResource toResource(SensorReading sensorReading) {
        return new SensorReadingResource(sensorReading.id(),
                sensorReading.sensorId(),
                sensorReading.reading(),
                sensorReading.unit(),
                sensorReading.createdAt());
    }
}
