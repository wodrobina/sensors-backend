package eu.wodrobina.rodos.sensorreading;

import java.util.List;
import java.util.Optional;

public interface SensorRepository {

    List<SensorReading> findAll();

    Optional<SensorReading> findById(long id);

    SensorReading save(SensorReading dto);
}
