package eu.wodrobina.rodos.rpc;

import eu.wodrobina.rodos.sensor.SensorService;
import eu.wodrobina.rodos.sensor.api.RegisterSensorRequest;
import eu.wodrobina.rodos.sensorreading.SensorReadingService;
import eu.wodrobina.rodos.sensorreading.SensorUnit;
import eu.wodrobina.rodos.sensorreading.api.SensorReadingRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static eu.wodrobina.rodos.rpc.TypeValidators.requireDecimal;
import static eu.wodrobina.rodos.rpc.TypeValidators.requireUnit;
import static eu.wodrobina.rodos.rpc.TypeValidators.requireUuid;

@Service
class DispatchService {

    private final SensorReadingService sensorReadingService;
    private final SensorService sensorService;

    public DispatchService(SensorReadingService sensorReadingService, SensorService sensorService) {
        this.sensorReadingService = sensorReadingService;
        this.sensorService = sensorService;
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

            default ->
                    throw new JsonRpcException(JsonRpcError.methodNotFound("Unknown method: " + method));
        };
    }
}
