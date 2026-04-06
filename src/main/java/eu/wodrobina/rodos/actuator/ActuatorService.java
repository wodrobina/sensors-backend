package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.ActuatorResource;
import eu.wodrobina.rodos.actuator.api.ActuatorScheduleResource;
import eu.wodrobina.rodos.actuator.api.RegisterActuatorRequest;
import eu.wodrobina.rodos.actuator.api.RegisterActuatorScheduleRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActuatorService {

    private final ActuatorRepository actuatorRepository;
    private final ScheduleRepository scheduleRepository;

    ActuatorService(ActuatorRepository actuatorRepository,
                    ScheduleRepository scheduleRepository) {
        this.actuatorRepository = actuatorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public ActuatorResource registerActuator(RegisterActuatorRequest request) {
        return actuatorRepository.save(request.actuatorName(), request.baseUrl())
                .asResource();
    }

    public void deleteActuator(UUID actuatorId) {
        actuatorRepository.deleteById(actuatorId);
    }

    public ActuatorScheduleResource addSchedule(RegisterActuatorScheduleRequest request) {
        return scheduleRepository.saveSchedule(new ActuatorId(request.actuatorId()),
                        request.activationTime(),
                        request.durationSeconds(),
                        request.enabled())
                .asResource();
    }

    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteSchedule(scheduleId);
    }
}
