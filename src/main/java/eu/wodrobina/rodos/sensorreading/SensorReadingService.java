package eu.wodrobina.rodos.sensorreading;

import eu.wodrobina.rodos.sensorreading.api.SensorReadingResource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class SensorReadingService {

    private final SensorRepository sensorRepository;

    public SensorReadingService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }
    public Collection<SensorReadingResource> findAllByName(String sensorName) {
        List<SensorReading> allBySensorName = sensorRepository.findAllBySensorName(sensorName);
        return allBySensorName.stream()
                .map(SensorReading::asResource)
                .toList();
    }


    public SensorReadingResource save(SensorReading sensorReading) {
        return sensorRepository.save(sensorReading).asResource();
    }
}
