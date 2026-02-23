package api;

import java.util.UUID;

public record SensorResource(UUID id, String sensorName, String sensorComment, String publicKey) {

}
