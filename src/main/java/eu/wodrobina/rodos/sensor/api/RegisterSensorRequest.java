package eu.wodrobina.rodos.sensor.api;

import org.jetbrains.annotations.NotNull;

public record RegisterSensorRequest(@NotNull String sensorName, String sensorComment,@NotNull String publicKey) {
}
