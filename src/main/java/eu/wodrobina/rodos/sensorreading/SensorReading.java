package eu.wodrobina.rodos.sensorreading;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents single sensor reading.
 */
public record SensorReading(
        Long id,
        String sensorName,
        BigDecimal reading,
        String unit
) {

    public SensorReading(Long id, String sensorName, BigDecimal reading, String unit) {
        this.id = id;
        this.sensorName = sensorName;
        this.reading = reading.setScale(2, RoundingMode.HALF_UP);
        this.unit = unit;
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

}
