package eu.wodrobina.rodos.sensorreading;

import eu.wodrobina.rodos.sensorreading.api.SensorReadingResource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/**
 * An entity that represents single sensor reading.
 */
public class SensorReading {

    private Long id;
    private String sensorName;
    private BigDecimal reading;
    private String unit;
    private Instant createdAt;

    SensorReading(Long id, String sensorName, BigDecimal reading, String unit) {
        this.id = id;
        this.sensorName = sensorName;
        this.reading = reading.setScale(2, RoundingMode.HALF_UP);
        this.unit = unit;
        this.createdAt = Instant.now();
    }

    public static SensorReading of(String sensorName,
                                   BigDecimal reading,
                                   SensorUnit sensorUnit) {
        return new SensorReading(null, sensorName, reading, sensorUnit.getUnit());
    }

    public static SensorReading of(String sensorName,
                                   double reading,
                                   SensorUnit sensorUnit) {
        return new SensorReading(null, sensorName, BigDecimal.valueOf(reading), sensorUnit.getUnit());
    }

    public Long id() {
        return id;
    }

    public String sensorName() {
        return sensorName;
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
                sensorReading.sensorName(),
                sensorReading.reading(),
                sensorReading.unit(),
                sensorReading.createdAt());
    }
}
