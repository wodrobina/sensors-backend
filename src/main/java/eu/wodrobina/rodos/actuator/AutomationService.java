package eu.wodrobina.rodos.actuator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
class AutomationService {

    private static final Logger logger = LoggerFactory.getLogger(AutomationService.class);

    private final ScheduleRepository scheduleRepository;
    private final ActuatorRepository actuatorRepository;
    private final ActuatorTimerService actuatorTimerService;
    private final SensorEvaluationService sensorEvaluationService;

    private final Set<String> processedExecutions =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    public AutomationService(
            ScheduleRepository scheduleRepository,
            ActuatorRepository actuatorRepository,
            ActuatorTimerService actuatorTimerService,
            SensorEvaluationService sensorEvaluationService
    ) {
        this.scheduleRepository = scheduleRepository;
        this.actuatorRepository = actuatorRepository;
        this.actuatorTimerService = actuatorTimerService;
        this.sensorEvaluationService = sensorEvaluationService;
    }

    @Scheduled(cron = "*/10 * * * * *")
        // Every 10 seconds
    void checkSchedules() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);

        logger.info("[Automation] Checking active windows for time: {}", now);

        List<ActuatorSchedule> activeSchedules =
                scheduleRepository.findSchedulesActiveAt(now);

        logger.info("[Automation] Found {} active schedules", activeSchedules.size());

        for (ActuatorSchedule schedule : activeSchedules) {
            String executionKey = buildExecutionKey(schedule, today);

            if (processedExecutions.contains(executionKey)) {
                logger.debug("[Automation] Schedule {} already processed for current window",
                        schedule.getScheduleId());
                continue;
            }

            boolean conditionsSatisfied =
                    sensorEvaluationService.areConditionsSatisfied(schedule.getScheduleId());

            if (!conditionsSatisfied) {
                logger.info("[Automation] Conditions not satisfied for schedule {}. Will retry next check.",
                        schedule.getScheduleId());
                continue;
            }

            ActuatorId actuatorId = schedule.getActuatorId();

            Optional<Actuator> actuatorOptional =
                    actuatorRepository.findById(actuatorId);

            if (actuatorOptional.isEmpty()) {
                logger.warn("[Automation] Actuator {} not found in database. Schedule {} skipped.",
                        actuatorId, schedule.getScheduleId());
                continue;
            }

            Actuator actuator = actuatorOptional.get();

            logger.info("[Automation] Triggering actuator: {} for {} seconds",
                    actuatorId, schedule.getDurationSeconds());

            actuatorTimerService.turnOnForDuration(
                    actuator,
                    schedule.getDurationSeconds()
            );

            processedExecutions.add(executionKey);
        }

        cleanUpProcessedExecutions(today);
    }

    private String buildExecutionKey(ActuatorSchedule schedule, LocalDate date) {
        return schedule.getScheduleId().id()
                + "_"
                + date
                + "_"
                + schedule.getActivationTime();
    }

    private void cleanUpProcessedExecutions(LocalDate today) {
        String todayMarker = "_" + today + "_";

        processedExecutions.removeIf(key -> !key.contains(todayMarker));
    }
}