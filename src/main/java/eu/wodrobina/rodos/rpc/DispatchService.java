package eu.wodrobina.rodos.rpc;

import eu.wodrobina.rodos.sensor.SensorService;
import eu.wodrobina.rodos.sensor.api.RegisterSensorRequest;
import eu.wodrobina.rodos.sensorreading.SensorReading;
import eu.wodrobina.rodos.sensorreading.SensorReadingService;
import eu.wodrobina.rodos.sensorreading.SensorUnit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import static eu.wodrobina.rodos.rpc.TypeValidators.requireDecimal;
import static eu.wodrobina.rodos.rpc.TypeValidators.requireString;
import static eu.wodrobina.rodos.rpc.TypeValidators.requireUnit;

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
                    sensorReadingService.findAllByName(requireString(params, "sensorName"));

            case "sensor.reading.create" -> {
                String sensorName = requireString(params, "sensorName");
                BigDecimal reading = requireDecimal(params, "reading");
                SensorUnit unit = requireUnit(params, "unit");

                yield sensorReadingService.save(SensorReading.of(sensorName, reading, unit));
            }

            case "sensor.register" ->
                    sensorService.registerSensor(RegisterSensorRequest.fromRequestParams(params));

            default ->
                    throw new JsonRpcException(JsonRpcError.methodNotFound("Unknown method: " + method));
        };
    }
}
