package eu.wodrobina.rodos.rpc;

import eu.wodrobina.rodos.actuator.ActuatorManagementService;
import eu.wodrobina.rodos.actuator.api.RegisterActuatorRequest;
import eu.wodrobina.rodos.actuator.api.RegisterActuatorScheduleRequest;
import eu.wodrobina.rodos.rpc.api.JsonRpcError;
import eu.wodrobina.rodos.rpc.exception.JsonRpcException;
import eu.wodrobina.rodos.sensor.SensorService;
import eu.wodrobina.rodos.sensor.api.RegisterSensorRequest;
import eu.wodrobina.rodos.sensorreading.SensorReadingService;
import eu.wodrobina.rodos.sensorreading.SensorUnit;
import eu.wodrobina.rodos.sensorreading.api.SensorReadingRequest;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
class DispatchService {

    private final SensorReadingService sensorReadingService;
    private final SensorService sensorService;
    private final ActuatorManagementService actuatorManagementService;

    public DispatchService(SensorReadingService sensorReadingService,
                           SensorService sensorService,
                           ActuatorManagementService actuatorManagementService) {
        this.sensorReadingService = sensorReadingService;
        this.sensorService = sensorService;
        this.actuatorManagementService = actuatorManagementService;
    }

    public Object dispatch(String method, Map<String, Object> params) {
        return switch (method) {
            case "sensor.reading.findAllByName" ->
                    sensorReadingService.findAllByReadingForSensor(requireUuid(params, "sensorId"));

            case "sensor.reading.create" -> {
                UUID sensorId = requireUuid(params, "sensorId");
                BigDecimal reading = requireDecimal(params, "reading");
                SensorUnit unit = requireUnit(params, "unit");

                yield sensorReadingService.save(SensorReadingRequest.of(sensorId, reading, unit));
            }

            case "sensor.register" ->
                    sensorService.registerSensor(RegisterSensorRequest.fromRequestParams(params));

            case "actuator.register" ->
                    actuatorManagementService.registerActuator(RegisterActuatorRequest.fromRequestParams(params));

            case "actuator.delete" ->
            {
                actuatorManagementService.deleteActuator(requireUuid(params, "actuatorId"));
                yield Map.of("status", "OK");
            }

            case "actuator.schedule.add" ->
                    actuatorManagementService.addSchedule(RegisterActuatorScheduleRequest.fromRequestParams(params));

            case "actuator.schedule.delete" ->
            {
                Long scheduleId = Long.parseLong(params.get("scheduleId").toString());
                actuatorManagementService.deleteSchedule(scheduleId);
                yield Map.of("status", "OK");
            }

            default ->
                    throw new JsonRpcException(JsonRpcError.methodNotFound("Unknown method: " + method));
        };
    }

    private UUID requireUuid(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required param: " + key);
        }
        return UUID.fromString(value.toString());
    }

    private BigDecimal requireDecimal(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required param: " + key);
        }
        return new BigDecimal(value.toString());
    }

    private SensorUnit requireUnit(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing required param: " + key);
        }
        return SensorUnit.valueOf(value.toString());
    }
}