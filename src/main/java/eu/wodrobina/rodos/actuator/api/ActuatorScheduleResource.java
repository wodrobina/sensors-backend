package eu.wodrobina.rodos.actuator.api;

import eu.wodrobina.rodos.actuator.ActuatorId;

import java.time.LocalTime;

public record ActuatorScheduleResource(Long id, ActuatorId actuatorId,
                                       LocalTime activationTime,
                                       int durationSeconds,
                                       boolean enabled) {
}
