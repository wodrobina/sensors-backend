package eu.wodrobina.rodos.actuator.api;

import eu.wodrobina.rodos.actuator.ActuatorId;

public record ActuatorResource(ActuatorId id,
                               String actuatorName,
                               String baseUrl) {

    public String buildOnUrl() {
        return baseUrl + "/" + id.id().toString() + "/on";
    }

    public String buildOffUrl() {
        return baseUrl + "/" + id.id().toString() + "/off";
    }
}
