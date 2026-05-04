package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.ActuatorScheduleResource;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.util.Objects;
import java.util.Set;

class ActuatorSchedule {

    private ScheduleId scheduleId;
    private ActuatorId actuatorId;
    private LocalTime activationTime;
    private Set<DayOfWeek> daysOfWeek;
    private Set<Month> months;
    private int durationSeconds;
    private boolean enabled;

    public ActuatorSchedule() {
    }

    public ActuatorSchedule(ScheduleId scheduleId, ActuatorId actuatorId, LocalTime activationTime, Set<DayOfWeek> daysOfWeek, Set<Month> months, int durationSeconds, boolean enabled) {
        this.scheduleId = scheduleId;
        this.actuatorId = actuatorId;
        this.activationTime = activationTime;
        this.daysOfWeek = daysOfWeek;
        this.months = months;
        this.durationSeconds = durationSeconds;
        this.enabled = enabled;
    }

    public ActuatorSchedule(ActuatorId actuatorName, LocalTime activationTime, Set<DayOfWeek> daysOfWeek, Set<Month> months, int durationSeconds, boolean enabled) {
        this.actuatorId = actuatorName;
        this.activationTime = activationTime;
        this.daysOfWeek = daysOfWeek;
        this.months = months;
        this.durationSeconds = durationSeconds;
        this.enabled = enabled;
    }

    public ScheduleId getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(ScheduleId scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ActuatorId getActuatorId() {
        return this.actuatorId;
    }

    public void setActuatorId(ActuatorId actuatorId) {
        this.actuatorId = actuatorId;
    }

    public LocalTime getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(LocalTime activationTime) {
        this.activationTime = activationTime;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<DayOfWeek> getDaysOfWeek() {
        return Set.copyOf(daysOfWeek);
    }

    public Set<Month> getMonths() {
        return Set.copyOf(months);
    }

    public ActuatorScheduleResource asResource() {
        return new ActuatorScheduleResource(this.scheduleId, this.actuatorId, this.activationTime, this.durationSeconds, this.enabled);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ActuatorSchedule that = (ActuatorSchedule) o;
        return Objects.equals(scheduleId, that.scheduleId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(scheduleId);
    }
}