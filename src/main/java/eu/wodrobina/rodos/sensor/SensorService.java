package eu.wodrobina.rodos.sensor;

import api.SensorResource;
import eu.wodrobina.rodos.sensor.api.RegisterSensorRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public SensorResource registerSensor(RegisterSensorRequest registerSensorRequest) {
        Optional<Sensor> byPublicKey = sensorRepository.findByPublicKey(registerSensorRequest.publicKey());

        if (byPublicKey.isPresent()) {
            throw new SensorAlreadyRegisteredException();
        }
        Sensor sensor = sensorRepository.save(Sensor.newSensor(registerSensorRequest.sensorName(),
                registerSensorRequest.sensorComment(),
                registerSensorRequest.publicKey()));

        return new SensorResource(sensor.getId(), sensor.getSensorName(), sensor.getSensorComment(), sensor.getPublicKey());
    }

    public Optional<SensorResource> findById(UUID uuid) {
        return sensorRepository.findById(uuid)
                .map(Sensor::asResource);
    }
}
