package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.ActuatorResource;
import eu.wodrobina.rodos.actuator.api.ActuatorScheduleResource;
import eu.wodrobina.rodos.actuator.api.RegisterActuatorRequest;
import eu.wodrobina.rodos.actuator.api.RegisterActuatorScheduleRequest;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActuatorService {

    public record ScheduledEvent(
        String actuatorName,
        int startSeconds,
        int endSeconds,
        String startTimeFormatted,
        String endTimeFormatted
    ) {}

    private final ActuatorRepository actuatorRepository;
    private final ScheduleRepository scheduleRepository;

    public ActuatorService(ActuatorRepository actuatorRepository,
                          ScheduleRepository scheduleRepository) {
        this.actuatorRepository = actuatorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public ActuatorResource registerActuator(RegisterActuatorRequest request) {
        return actuatorRepository.save(new Actuator(request.actuatorName(), request.baseUrl()))
                .asResource();
    }

    public void deleteActuator(ActuatorId actuatorId) {
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

    public List<ActuatorResource> getAllActuators() {
        return actuatorRepository.findAll()
                .stream()
                .map(Actuator::asResource)
                .toList();
    }

}
