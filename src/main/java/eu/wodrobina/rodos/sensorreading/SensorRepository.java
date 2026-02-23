package eu.wodrobina.rodos.sensorreading;

import java.time.Instant;
import java.util.List;

interface SensorRepository {

    List<SensorReading> findAllBySensorName(String sensorName);

    List<SensorReading> findAllBySensorName(String sensorName, Instant from, Instant to);

    SensorReading save(SensorReading dto);
}
