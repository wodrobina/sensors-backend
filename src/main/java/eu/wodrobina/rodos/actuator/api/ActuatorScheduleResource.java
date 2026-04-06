package eu.wodrobina.rodos.actuator.api;

import eu.wodrobina.rodos.actuator.ActuatorId;
import eu.wodrobina.rodos.actuator.ScheduleId;

import java.time.LocalTime;

public record ActuatorScheduleResource(ScheduleId scheduleId, ActuatorId actuatorId,
                                       LocalTime activationTime,
                                       int durationSeconds,
                                       boolean enabled) {
}
