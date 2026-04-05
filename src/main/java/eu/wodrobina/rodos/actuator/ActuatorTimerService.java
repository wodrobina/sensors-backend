package eu.wodrobina.rodos.actuator;

import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ActuatorTimerService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final ActuatorHttpService actuatorHttpService;

    public ActuatorTimerService(ActuatorHttpService actuatorHttpService) {
        this.actuatorHttpService = actuatorHttpService;
    }

    public void turnOnForDuration(Actuator actuator, int durationSeconds) {
        actuatorHttpService.turnOn(actuator);

        scheduler.schedule(() -> {
            actuatorHttpService.turnOff(actuator);
        }, durationSeconds, TimeUnit.SECONDS);
    }
}
