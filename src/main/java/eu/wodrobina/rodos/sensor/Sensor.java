package eu.wodrobina.rodos.sensor;

import api.SensorResource;

import java.util.Objects;
import java.util.UUID;

class Sensor {
    private UUID id;
    private String sensorName;
    private String sensorComment;
    private String publicKey;

    Sensor(UUID id, String sensorName, String sensorComment, String publicKey) {
        this.id = id;
        this.sensorName = sensorName;
        this.sensorComment = sensorComment;
        this.publicKey = publicKey;
    }

    public static Sensor newSensor(String sensorName, String sensorComment, String publicKey) {
        return new Sensor(null, sensorName, sensorComment, publicKey);
    }

    public UUID getId() {
        return id;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorComment() {
        return sensorComment;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public SensorResource asResource() {
        return new SensorResource(this.getId(), this.getSensorName(), this.getSensorComment(), this.getPublicKey());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Sensor that = (Sensor) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
