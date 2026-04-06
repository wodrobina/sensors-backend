package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.sensorreading.SensorReadingService;
import eu.wodrobina.rodos.sensorreading.api.SensorReadingResource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
class SensorEvaluationService {

    private final ActuatorConditionRepository conditionRepository;
    private final SensorReadingService sensorReadingService;

    public SensorEvaluationService(ActuatorConditionRepository conditionRepository,
                                   SensorReadingService sensorReadingService) {
        this.conditionRepository = conditionRepository;
        this.sensorReadingService = sensorReadingService;
    }

    boolean areConditionsSatisfied(ScheduleId scheduleId) {
        List<ActuatorCondition> conditions = conditionRepository.findEnabledByScheduleId(scheduleId);

        for (ActuatorCondition condition : conditions) {
            Optional<BigDecimal> currentValue = sensorReadingService.findLatestBySensorId(condition.getSensorId())
                    .map(SensorReadingResource::reading);

            if (currentValue.isEmpty()) {
                return false;
            }

            if (!matches(currentValue.get(), condition.getOperator(), condition.getExpectedValue())) {
                return false;
            }
        }

        return true;
    }

    private static boolean matches(BigDecimal actual,
                                   ComparisonOperator operator,
                                   BigDecimal expected) {
        int cmp = actual.compareTo(expected);

        return switch (operator) {
            case GREATER_THAN -> cmp > 0;
            case GREATER_OR_EQUAL -> cmp >= 0;
            case LESS_THAN -> cmp < 0;
            case LESS_OR_EQUAL -> cmp <= 0;
            case EQUAL -> cmp == 0;
        };
    }
}
