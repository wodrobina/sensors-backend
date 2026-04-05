package eu.wodrobina.rodos.actuator;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AutomationService {

    private final ScheduleRepository repository;
    private final ActuatorTimerService actuatorTimerService;
    private final Map<UUID, Actuator> registeredActuators = new ConcurrentHashMap<>();

    public AutomationService(ScheduleRepository repository,
                             ActuatorTimerService actuatorTimerService) {
        this.repository = repository;
        this.actuatorTimerService = actuatorTimerService;
    }

    public void registerActuator(Actuator actuator) {
        registeredActuators.put(actuator.getId(), actuator);
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkSchedules() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        System.out.println("[Automation] Sprawdzanie harmonogramów dla godziny: " + now);

        List<ActuatorSchedule> activeSchedules = repository.findSchedulesForTime(now);

        for (ActuatorSchedule schedule : activeSchedules) {
            UUID actuatorId = schedule.getActuatorId().id();
            Actuator actuator = registeredActuators.get(actuatorId);

            if (actuator != null) {
                System.out.println("[Automation] Włączam actuator: " + actuatorId +
                        " na " + schedule.getDurationSeconds() + " sekund");

                actuatorTimerService.turnOnForDuration(actuator, schedule.getDurationSeconds());
            } else {
                System.err.println("[Automation] Brak zarejestrowanego aktuatora: " + actuatorId);
            }
        }
    }
}