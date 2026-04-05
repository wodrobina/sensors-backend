package eu.wodrobina.rodos.actuator.api;

import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

public record RegisterActuatorScheduleRequest(
        UUID actuatorId,
        LocalTime activationTime,
        int durationSeconds,
        boolean enabled
) {
    public static RegisterActuatorScheduleRequest fromRequestParams(Map<String, Object> params) {
        return new RegisterActuatorScheduleRequest(
                UUID.fromString(params.get("actuatorId").toString()),
                LocalTime.parse(params.get("activationTime").toString()),
                Integer.parseInt(params.get("durationSeconds").toString()),
                params.get("enabled") == null || Boolean.parseBoolean(params.get("enabled").toString())
        );
    }
}
