package eu.wodrobina.rodos.rpc;

import eu.wodrobina.rodos.sensorreading.SensorUnit;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class TypeValidators {

    private TypeValidators() {
    }

    static String requireString(Map<String, Object> params, String key) {
        if (params == null) {
            throw new JsonRpcException(JsonRpcError.invalidParams("params is required"));
        }
        Object v = params.get(key);
        if (!(v instanceof String s) || !StringUtils.hasText(s)) {
            throw new JsonRpcException(JsonRpcError.invalidParams("'" + key + "' must be a non-empty string"));
        }
        return s;
    }

    static BigDecimal requireDecimal(Map<String, Object> params, String key) {
        if (params == null) {
            throw new JsonRpcException(JsonRpcError.invalidParams("params is required"));
        }
        Object v = params.get(key);
        switch (v) {
            case null -> throw new JsonRpcException(JsonRpcError.invalidParams("'" + key + "' is required"));
            case Number n -> {
                return new BigDecimal(n.toString());
            }
            case String s when StringUtils.hasText(s) -> {
                try {
                    return new BigDecimal(s);
                } catch (NumberFormatException ex) {
                    throw new JsonRpcException(JsonRpcError.invalidParams("'" + key + "' must be a decimal number"));
                }
            }
            default -> {
            }
        }
        throw new JsonRpcException(JsonRpcError.invalidParams("'" + key + "' must be a decimal number"));
    }

    static SensorUnit requireUnit(Map<String, Object> params, String key) {
        if (params == null) {
            throw new JsonRpcException(JsonRpcError.invalidParams("params is required"));
        }
        Object v = params.get(key);
        if (!(v instanceof String s) || !StringUtils.hasText(s)) {
            throw new JsonRpcException(JsonRpcError.invalidParams("'" + key + "' must be a string"));
        }

        // Accept "CELSIUS" and "°C"
        if (Objects.equals(s, SensorUnit.CELSIUS.getUnit())
                || Objects.equals(s, "C")) {
            return SensorUnit.CELSIUS;
        }

        try {
            return SensorUnit.valueOf(s);
        } catch (IllegalArgumentException ex) {
            throw new JsonRpcException(JsonRpcError.invalidParams("Unknown unit: " + s));
        }
    }
}
