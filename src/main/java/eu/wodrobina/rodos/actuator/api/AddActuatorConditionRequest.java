package eu.wodrobina.rodos.actuator.api;

import eu.wodrobina.rodos.actuator.ComparisonOperator;
import eu.wodrobina.rodos.actuator.ScheduleId;
import eu.wodrobina.rodos.rpc.api.JsonRpcError;
import eu.wodrobina.rodos.rpc.exception.JsonRpcException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static eu.wodrobina.rodos.rpc.TypeValidators.requireDecimal;
import static eu.wodrobina.rodos.rpc.TypeValidators.requireString;
import static eu.wodrobina.rodos.rpc.TypeValidators.requireUuid;

public record AddActuatorConditionRequest(
        ScheduleId scheduleId,
        UUID sensorId,
        SensorReadingType readingType,
        ComparisonOperator operator,
        BigDecimal expectedValue,
        boolean enabled
) {

    public static AddActuatorConditionRequest fromRequestParams(Map<String, Object> params) {
        UUID scheduleUuid = requireUuid(params, "scheduleId");
        UUID sensorId = requireUuid(params, "sensorId");
        BigDecimal expectedValue = requireDecimal(params, "expectedValue");

        String readingTypeRaw = requireString(params, "readingType");
        String operatorRaw = requireString(params, "operator");

        SensorReadingType readingType;
        ComparisonOperator operator;

        try {
            readingType = SensorReadingType.valueOf(readingTypeRaw);
        } catch (IllegalArgumentException e) {
            throw new JsonRpcException(JsonRpcError.invalidParams("Unknown readingType: " + readingTypeRaw));
        }

        try {
            operator = ComparisonOperator.valueOf(operatorRaw);
        } catch (IllegalArgumentException e) {
            throw new JsonRpcException(JsonRpcError.invalidParams("Unknown operator: " + operatorRaw));
        }

        boolean enabled = params.get("enabled") == null || Boolean.parseBoolean(params.get("enabled").toString());

        return new AddActuatorConditionRequest(
                new ScheduleId(scheduleUuid),
                sensorId,
                readingType,
                operator,
                expectedValue,
                enabled
        );
    }
}
