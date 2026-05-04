package eu.wodrobina.rodos.actuator.api;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public record RegisterActuatorScheduleRequest(
        UUID actuatorId,
        LocalTime activationTime,
        Set<DayOfWeek> daysOfWeek,
        Set<Month> months,
        int durationSeconds,
        boolean enabled
) {
    public static RegisterActuatorScheduleRequest fromRequestParams(Map<String, Object> params) {
        return new RegisterActuatorScheduleRequest(
                UUID.fromString(params.get("actuatorId").toString()),
                LocalTime.parse(params.get("activationTime").toString()),
                parseSet(
                        params.get("daysOfWeek"),
                        DayOfWeek::valueOf,
                        Set.of(DayOfWeek.values())
                ),
                parseSet(
                        params.get("months"),
                        Month::valueOf,
                        Set.of(Month.values())
                ),
                Integer.parseInt(params.get("durationSeconds").toString()),
                params.get("enabled") == null || Boolean.parseBoolean(params.get("enabled").toString())
        );
    }

    private static <T> Set<T> parseSet(
            Object value,
            Function<String, T> mapper,
            Set<T> defaultValue
    ) {
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Iterable<?> iterable) {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .map(String::toUpperCase)
                    .map(mapper)
                    .collect(Collectors.toSet());
        }

        return Arrays.stream(value.toString().split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .map(String::toUpperCase)
                .map(mapper)
                .collect(Collectors.toSet());
    }
}
