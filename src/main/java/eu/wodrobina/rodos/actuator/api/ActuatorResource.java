package eu.wodrobina.rodos.actuator.api;

import eu.wodrobina.rodos.actuator.ActuatorId;

public record ActuatorResource(ActuatorId id,
                               String actuatorName,
                               String baseUrl) {
}
