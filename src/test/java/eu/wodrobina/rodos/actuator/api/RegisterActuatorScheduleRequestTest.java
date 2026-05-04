package eu.wodrobina.rodos.actuator.api;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterActuatorScheduleRequestTest {

    @Test
    void shouldParseRequestWithDaysOfWeekAndMonths() {
        UUID actuatorId = UUID.randomUUID();

        Map<String, Object> params = Map.of(
                "actuatorId", actuatorId.toString(),
                "activationTime", "06:30",
                "daysOfWeek", List.of("MONDAY", "WEDNESDAY", "FRIDAY"),
                "months", List.of("JUNE", "JULY", "AUGUST"),
                "durationSeconds", "3600",
                "enabled", "true"
        );

        RegisterActuatorScheduleRequest request =
                RegisterActuatorScheduleRequest.fromRequestParams(params);

        assertEquals(actuatorId, request.actuatorId());
        assertEquals(LocalTime.of(6, 30), request.activationTime());
        assertEquals(
                Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                request.daysOfWeek()
        );
        assertEquals(
                Set.of(Month.JUNE, Month.JULY, Month.AUGUST),
                request.months()
        );
        assertEquals(3600, request.durationSeconds());
        assertTrue(request.enabled());
    }

    @Test
    void shouldUseAllDaysOfWeekAndAllMonthsWhenMissing() {
        UUID actuatorId = UUID.randomUUID();

        Map<String, Object> params = Map.of(
                "actuatorId", actuatorId.toString(),
                "activationTime", "06:30",
                "durationSeconds", 1800
        );

        RegisterActuatorScheduleRequest request =
                RegisterActuatorScheduleRequest.fromRequestParams(params);

        assertEquals(actuatorId, request.actuatorId());
        assertEquals(LocalTime.of(6, 30), request.activationTime());
        assertEquals(Set.of(DayOfWeek.values()), request.daysOfWeek());
        assertEquals(Set.of(Month.values()), request.months());
        assertEquals(1800, request.durationSeconds());

        // zgodnie z Twoim kodem: brak "enabled" oznacza true
        assertTrue(request.enabled());
    }

    @Test
    void shouldParseCommaSeparatedDaysOfWeekAndMonths() {
        UUID actuatorId = UUID.randomUUID();

        Map<String, Object> params = Map.of(
                "actuatorId", actuatorId.toString(),
                "activationTime", "05:00",
                "daysOfWeek", "MONDAY, THURSDAY, SATURDAY",
                "months", "APRIL, MAY, SEPTEMBER",
                "durationSeconds", "2400",
                "enabled", "false"
        );

        RegisterActuatorScheduleRequest request =
                RegisterActuatorScheduleRequest.fromRequestParams(params);

        assertEquals(
                Set.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY),
                request.daysOfWeek()
        );
        assertEquals(
                Set.of(Month.APRIL, Month.MAY, Month.SEPTEMBER),
                request.months()
        );
        assertEquals(2400, request.durationSeconds());
        assertFalse(request.enabled());
    }

    @Test
    void shouldParseLowercaseDaysOfWeekAndMonths() {
        UUID actuatorId = UUID.randomUUID();

        Map<String, Object> params = Map.of(
                "actuatorId", actuatorId.toString(),
                "activationTime", "07:15",
                "daysOfWeek", List.of("monday", "wednesday"),
                "months", List.of("june", "august"),
                "durationSeconds", 1200
        );

        RegisterActuatorScheduleRequest request =
                RegisterActuatorScheduleRequest.fromRequestParams(params);

        assertEquals(
                Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                request.daysOfWeek()
        );
        assertEquals(
                Set.of(Month.JUNE, Month.AUGUST),
                request.months()
        );
        assertEquals(1200, request.durationSeconds());
        assertTrue(request.enabled());
    }

    @Test
    void shouldThrowExceptionForInvalidDayOfWeek() {
        UUID actuatorId = UUID.randomUUID();

        Map<String, Object> params = Map.of(
                "actuatorId", actuatorId.toString(),
                "activationTime", "06:00",
                "daysOfWeek", List.of("FUNDAY"),
                "months", List.of("JUNE"),
                "durationSeconds", 3600
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> RegisterActuatorScheduleRequest.fromRequestParams(params)
        );
    }

    @Test
    void shouldThrowExceptionForInvalidMonth() {
        UUID actuatorId = UUID.randomUUID();

        Map<String, Object> params = Map.of(
                "actuatorId", actuatorId.toString(),
                "activationTime", "06:00",
                "daysOfWeek", List.of("MONDAY"),
                "months", List.of("RAINY"),
                "durationSeconds", 3600
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> RegisterActuatorScheduleRequest.fromRequestParams(params)
        );
    }
}