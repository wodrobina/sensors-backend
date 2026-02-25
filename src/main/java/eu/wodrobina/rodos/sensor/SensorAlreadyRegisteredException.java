package eu.wodrobina.rodos.sensor;

public class SensorAlreadyRegisteredException extends RuntimeException {
    public SensorAlreadyRegisteredException() {
        super("Sensor with public key already exist.");
    }
}
