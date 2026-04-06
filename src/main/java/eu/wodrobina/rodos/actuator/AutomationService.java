package eu.wodrobina.rodos.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AutomationService.class);

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
    void checkSchedules() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        logger.info("[Automation] Check schedule for time: {}", now);

        List<ActuatorSchedule> activeSchedules = repository.findSchedulesForTime(now);

        for (ActuatorSchedule schedule : activeSchedules) {
            UUID actuatorId = schedule.getActuatorId().id();
            Actuator actuator = registeredActuators.get(actuatorId);

            if (actuator != null) {
                logger.info("[Automation] Turning on actuator: {} for {} seconds", actuatorId, schedule.getDurationSeconds());

                actuatorTimerService.turnOnForDuration(actuator, schedule.getDurationSeconds());
            }
        }
    }
}