package eu.wodrobina.rodos.sensor.api;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static eu.wodrobina.rodos.rpc.TypeValidators.requireString;

public record RegisterSensorRequest(@NotNull String sensorName, String sensorComment, @NotNull String publicKey) {

    public static RegisterSensorRequest fromRequestParams(Map<String, Object> params) {
        return new RegisterSensorRequest(
                requireString(params, "sensorName"),
                getSensorComment(params),
                requireString(params, "publicKey")
        );
    }

    private static String getSensorComment(Map<String, Object> params) {
        if (params.containsKey("sensorComment")) {
            return params.get("sensorComment").toString();
        }
        return null;
    }
}
