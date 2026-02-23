package eu.wodrobina.rodos.sensorreading.api;

import eu.wodrobina.rodos.sensorreading.SensorReading;
import eu.wodrobina.rodos.sensorreading.SensorReadingService;
import eu.wodrobina.rodos.sensorreading.SensorUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

import static eu.wodrobina.rodos.sensorreading.api.TypeValidators.*;
import static eu.wodrobina.rodos.sensorreading.api.TypeValidators.requireUnit;

@RestController
@RequestMapping("/rpc")
public class SensorReadingController {

    private final SensorReadingService sensorReadingService;

    public SensorReadingController(SensorReadingService sensorReadingService) {
        this.sensorReadingService = sensorReadingService;
    }

    @PostMapping
    public ResponseEntity<JsonRpcResponse> rpc(@RequestBody JsonRpcRequest request) {
        Object id = request != null ? request.id() : null;

        try {
            if (request == null) {
                return ResponseEntity.ok(JsonRpcResponse.error(null, JsonRpcError.invalidRequest("Request body is null")));
            }
            if (!"2.0".equals(request.jsonrpc())) {
                return ResponseEntity.ok(JsonRpcResponse.error(id, JsonRpcError.invalidRequest("jsonrpc must be '2.0'")));
            }
            if (!StringUtils.hasText(request.method())) {
                return ResponseEntity.ok(JsonRpcResponse.error(id, JsonRpcError.invalidRequest("method is required")));
            }

            Object result = dispatch(request.method(), request.params());
            return ResponseEntity.ok(JsonRpcResponse.ok(id, result));

        } catch (JsonRpcException e) {
            return ResponseEntity.ok(JsonRpcResponse.error(id, e.error()));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonRpcResponse.error(id, JsonRpcError.internalError(e.getMessage())));
        }
    }

    private Object dispatch(String method, Map<String, Object> params) {
        return switch (method) {
            case "sensor.reading.findAllByName" -> sensorReadingService.findAllByName(requireString(params, "sensorName"));

            case "sensor.reading.create" -> {
                String sensorName = requireString(params, "sensorName");
                BigDecimal reading = requireDecimal(params, "reading");
                SensorUnit unit = requireUnit(params, "unit");

                yield sensorReadingService.save(SensorReading.of(sensorName, reading, unit));
            }

            default -> throw new JsonRpcException(JsonRpcError.methodNotFound("Unknown method: " + method));
        };
    }

    // --- JSON-RPC DTOs ---

    public record JsonRpcRequest(String jsonrpc, Object id, String method, Map<String, Object> params) {}

    public record JsonRpcResponse(String jsonrpc, Object id, Object result, JsonRpcError error) {
        public static JsonRpcResponse ok(Object id, Object result) {
            return new JsonRpcResponse("2.0", id, result, null);
        }

        public static JsonRpcResponse error(Object id, JsonRpcError error) {
            return new JsonRpcResponse("2.0", id, null, error);
        }
    }

    public record JsonRpcError(int code, String message, Object data) {
        public static JsonRpcError invalidRequest(String msg) {
            return new JsonRpcError(-32600, msg, null);
        }

        public static JsonRpcError methodNotFound(String msg) {
            return new JsonRpcError(-32601, msg, null);
        }

        public static JsonRpcError invalidParams(String msg) {
            return new JsonRpcError(-32602, msg, null);
        }

        public static JsonRpcError internalError(String msg) {
            return new JsonRpcError(-32603, msg != null ? msg : "Internal error", null);
        }
    }

    static class JsonRpcException extends RuntimeException {
        private final JsonRpcError error;

        JsonRpcException(JsonRpcError error) {
            super(error != null ? error.message : null);
            this.error = error;
        }

        JsonRpcError error() {
            return error;
        }
    }
}
