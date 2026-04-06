package eu.wodrobina.rodos.sensorreading;

import eu.wodrobina.rodos.sensor.SensorService;
import eu.wodrobina.rodos.sensor.exception.SensorMissingException;
import eu.wodrobina.rodos.sensorreading.api.SensorReadingRequest;
import eu.wodrobina.rodos.sensorreading.api.SensorReadingResource;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SensorReadingService {

    private final SensorReadingRepository sensorRepository;
    private final SensorService sensorService;

    SensorReadingService(SensorReadingRepository sensorRepository, SensorService sensorService) {
        this.sensorRepository = sensorRepository;
        this.sensorService = sensorService;
    }

    public Collection<SensorReadingResource> findAllByReadingForSensor(UUID sensorId) {
        List<SensorReading> allBySensorName = sensorRepository.findAllBySensorId(sensorId);
        return allBySensorName.stream()
                .map(SensorReading::asResource)
                .toList();
    }

    public Optional<SensorReadingResource> findLatestBySensorId(UUID sensorId) {
        return sensorRepository.findLatestBySensorId(sensorId)
                .map(SensorReading::asResource);
    }

    public SensorReadingResource save(SensorReadingRequest sensorReadingRequest) {
        sensorService.findById(sensorReadingRequest.sensorId())
                .orElseThrow(SensorMissingException::new);

        SensorReading savedReading = sensorRepository.save(SensorReading.from(sensorReadingRequest));

        return savedReading.asResource();
    }
}
