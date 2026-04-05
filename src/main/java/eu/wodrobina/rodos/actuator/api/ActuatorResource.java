package eu.wodrobina.rodos.actuator.api;

import java.util.UUID;

public record ActuatorResource(UUID id,
                               String actuatorName,
                               String baseUrl) {
}
