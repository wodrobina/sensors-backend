package eu.wodrobina.rodos.actuator.api;

import java.util.Map;

public record RegisterActuatorRequest(String actuatorName, String baseUrl) {

    public static RegisterActuatorRequest fromRequestParams(Map<String, Object> params) {
        return new RegisterActuatorRequest(
                params.get("actuatorName").toString(),
                params.get("baseUrl").toString()
        );
    }
}
