package eu.wodrobina.rodos.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
class AutomationService {

    private static final Logger logger = LoggerFactory.getLogger(AutomationService.class);

    private final ScheduleRepository repository;
    private final ActuatorTimerService actuatorTimerService;
    private final SensorEvaluationService sensorEvaluationService;
    private final Map<ActuatorId, Actuator> registeredActuators = new ConcurrentHashMap<>();

    private final Set<String> processedExecutions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public AutomationService(ScheduleRepository repository,
                             ActuatorTimerService actuatorTimerService, SensorEvaluationService sensorEvaluationService) {
        this.repository = repository;
        this.actuatorTimerService = actuatorTimerService;
        this.sensorEvaluationService = sensorEvaluationService;
    }

    public void registerActuator(Actuator actuator) {
        registeredActuators.put(actuator.getActuatorId(), actuator);
    }

    @Scheduled(cron = "*/10 * * * * *") // Every 10 seconds
    void checkSchedules() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        logger.info("[Automation] Checking active windows for time: {}", now);

        List<ActuatorSchedule> activeSchedules = repository.findSchedulesActiveAt(now);

        for (ActuatorSchedule schedule : activeSchedules) {
            String executionKey = schedule.getScheduleId().toString() + "_" + schedule.getActivationTime().toString();

            if (processedExecutions.contains(executionKey)) {
                continue;
            }

            if (!sensorEvaluationService.areConditionsSatisfied(schedule.getScheduleId())) {
                logger.info("[Automation] Conditions not satisfied for schedule {}. Will retry next minute.",
                        schedule.getScheduleId());
                continue;
            }

            ActuatorId actuatorId = schedule.getActuatorId();
            Actuator actuator = registeredActuators.get(actuatorId);

            if (actuator != null) {
                logger.info("[Automation] Triggering actuator: {} for {} seconds",
                        actuatorId, schedule.getDurationSeconds());

                actuatorTimerService.turnOnForDuration(actuator, schedule.getDurationSeconds());

                processedExecutions.add(executionKey);
            }
        }

        cleanUpProcessedExecutions();
    }

    private void cleanUpProcessedExecutions() {
        processedExecutions.removeIf(key -> false);
    }

}