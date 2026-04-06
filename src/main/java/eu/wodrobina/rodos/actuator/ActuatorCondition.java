package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.SensorReadingType;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ActuatorCondition {
    private UUID id;
    private UUID scheduleId;
    private UUID sensorId;
    private SensorReadingType readingType;
    private ComparisonOperator operator;
    private BigDecimal expectedValue;
    private boolean enabled;

    public ActuatorCondition(UUID id, UUID scheduleId, UUID sensorId, SensorReadingType readingType, ComparisonOperator operator, BigDecimal expectedValue, boolean enabled) {
        this.id = id;
        this.scheduleId = scheduleId;
        this.sensorId = sensorId;
        this.readingType = readingType;
        this.operator = operator;
        this.expectedValue = expectedValue;
        this.enabled = enabled;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(UUID scheduleId) {
        this.scheduleId = scheduleId;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    public SensorReadingType getReadingType() {
        return readingType;
    }

    public void setReadingType(SensorReadingType readingType) {
        this.readingType = readingType;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public void setOperator(ComparisonOperator operator) {
        this.operator = operator;
    }

    public BigDecimal getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(BigDecimal expectedValue) {
        this.expectedValue = expectedValue;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ActuatorCondition that = (ActuatorCondition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
