package eu.wodrobina.rodos.sensor;

import java.util.UUID;

public class Sensor {
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
}
