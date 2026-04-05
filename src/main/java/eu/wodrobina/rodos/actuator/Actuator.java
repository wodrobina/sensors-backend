package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.ActuatorResource;

import java.util.Objects;
import java.util.UUID;

class Actuator {

    private UUID id;
    private String actuatorName;
    private String baseUrl;

    public Actuator() {
    }

    public Actuator(String actuatorName, String baseUrl) {
        this.actuatorName = actuatorName;
        this.baseUrl = baseUrl;
    }

    public Actuator(UUID id, String actuatorName, String baseUrl) {
        this.id = id;
        this.actuatorName = actuatorName;
        this.baseUrl = baseUrl;
    }

    public UUID getId() {
        return id;
    }

    public ActuatorId getActuatorId() {
        return new ActuatorId(this.id);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getActuatorName() {
        return actuatorName;
    }

    public void setActuatorName(String actuatorName) {
        this.actuatorName = actuatorName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = normalizeBaseUrl(baseUrl);
    }

    public String buildOnUrl() {
        return baseUrl + "/" + id + "/ON";
    }

    public String buildOffUrl() {
        return baseUrl + "/" + id + "/OFF";
    }

    private String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("baseUrl cannot be null or blank");
        }

        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }

        return url;
    }

    public ActuatorResource asResource() {
        return new ActuatorResource(this.id, this.actuatorName, this.baseUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Actuator that = (Actuator) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Actuator{" +
                "id=" + id +
                ", actuatorName='" + actuatorName + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}