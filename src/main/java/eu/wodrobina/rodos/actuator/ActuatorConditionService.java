package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.AddActuatorConditionRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActuatorConditionService {

    private final ActuatorConditionRepository repository;

    public ActuatorConditionService(ActuatorConditionRepository repository) {
        this.repository = repository;
    }

    public ActuatorCondition addCondition(AddActuatorConditionRequest request) {
        ActuatorCondition condition = new ActuatorCondition(
                null,
                request.scheduleId().id(),
                request.sensorId(),
                request.readingType(),
                request.operator(),
                request.expectedValue(),
                request.enabled()
        );

        return repository.save(condition);
    }

    public void deleteCondition(UUID conditionId) {
        repository.deleteById(conditionId);
    }
}