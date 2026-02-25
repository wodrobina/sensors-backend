package eu.wodrobina.rodos.sensorreading;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface SensorReadingRepository {

    List<SensorReading> findAllBySensorId(UUID sensorId);

    List<SensorReading> findAllBySensorId(UUID sensorId, Instant from, Instant to);

    SensorReading save(SensorReading dto);
}
