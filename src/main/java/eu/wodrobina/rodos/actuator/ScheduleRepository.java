package eu.wodrobina.rodos.actuator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

record ActuatorScheduleWithDetails(
        String actuatorName,
        LocalTime startTime,
        LocalTime endTime
) {
}

@Repository
class ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ActuatorSchedule> rowMapper = (rs, rowNum) ->
            new ActuatorSchedule(
                    new ScheduleId(UUID.fromString(rs.getString("id"))),
                    new ActuatorId(UUID.fromString(rs.getString("actuator_id"))),
                    rs.getTime("activation_time").toLocalTime(),
                    parseDaysOfWeek(rs.getString("days_of_week")),
                    parseMonths(rs.getString("months")),
                    rs.getInt("duration_seconds"),
                    rs.getBoolean("enabled")
            );

    public List<ActuatorSchedule> findSchedulesForTime(LocalTime time) {
        String sql = """
                SELECT id, actuator_id, activation_time, duration_seconds, enabled
                FROM actuator_schedules
                WHERE enabled = true
                  AND activation_time = ?
                """;

        return jdbcTemplate.query(sql, rowMapper, Time.valueOf(time));
    }

    public ActuatorSchedule saveSchedule(ActuatorId actuatorId,
                                         LocalTime activationTime,
                                         Set<DayOfWeek> dayOfWeeks,
                                         Set<Month> months,
                                         int durationSeconds,
                                         boolean enabled) {
        String sql = """
                INSERT INTO actuator_schedules (
                    actuator_id,
                    activation_time,
                    days_of_week,
                    months,
                    duration_seconds,
                    enabled
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        Set<DayOfWeek> resolvedDaysOfWeek = dayOfWeeks == null || dayOfWeeks.isEmpty()
                ? Set.of(DayOfWeek.values())
                : dayOfWeeks;

        Set<Month> resolvedMonths = months == null || months.isEmpty()
                ? Set.of(Month.values())
                : months;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, actuatorId.id());
            ps.setTime(2, Time.valueOf(activationTime));
            ps.setString(3, serializeDaysOfWeek(resolvedDaysOfWeek));
            ps.setString(4, serializeMonths(resolvedMonths));
            ps.setInt(5, durationSeconds);
            ps.setBoolean(6, enabled);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys == null || !keys.containsKey("ID")) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }

        UUID key = (UUID) keys.get("ID");

        return new ActuatorSchedule(
                new ScheduleId(key),
                actuatorId,
                activationTime,
                resolvedDaysOfWeek,
                resolvedMonths,
                durationSeconds,
                enabled
        );
    }

    private String serializeDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        return daysOfWeek.stream()
                .sorted()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(","));
    }

    private String serializeMonths(Set<Month> months) {
        return months.stream()
                .sorted()
                .map(Month::name)
                .collect(Collectors.joining(","));
    }

    public List<ActuatorSchedule> findSchedulesActiveAt(LocalDateTime now) {
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek currentDayOfWeek = now.getDayOfWeek();
        Month currentMonth = now.getMonth();

        String sql = """
            SELECT id,
                   actuator_id,
                   activation_time,
                   days_of_week,
                   months,
                   duration_seconds,
                   enabled
            FROM actuator_schedules
            WHERE enabled = true
              AND activation_time <= ?
              AND (activation_time + (duration_seconds * interval '1 second')) > ?
              AND CONCAT(',', days_of_week, ',') LIKE ?
              AND CONCAT(',', months, ',') LIKE ?
            ORDER BY activation_time
            """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                Time.valueOf(currentTime),
                Time.valueOf(currentTime),
                "%," + currentDayOfWeek.name() + ",%",
                "%," + currentMonth.name() + ",%"
        );
    }

    public void deleteSchedule(Long scheduleId) {
        String sql = "DELETE FROM actuator_schedules WHERE id = ?";
        jdbcTemplate.update(sql, scheduleId);
    }

    public void deleteByActuatorId(UUID actuatorId) {
        String sql = "DELETE FROM actuator_schedules WHERE actuator_id = ?";
        jdbcTemplate.update(sql, actuatorId.toString());
    }

    public List<ActuatorScheduleWithDetails> findAllSchedulesWithDetails() {
        String sql = """
                SELECT a.actuator_name, s.activation_time, 
                       (s.activation_time + (s.duration_seconds * interval '1 second'))::time AS end_time
                FROM actuator_schedules s
                JOIN actuator a ON s.actuator_id = a.id
                WHERE s.enabled = true
                ORDER BY activation_time ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new ActuatorScheduleWithDetails(
                        rs.getString("actuator_name"),
                        rs.getTime("activation_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime()
                ));
    }

    private Set<DayOfWeek> parseDaysOfWeek(String value) {
        if (value == null || value.isBlank()) {
            return Set.of(DayOfWeek.values());
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(day -> !day.isBlank())
                .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                .collect(Collectors.toSet());
    }

    private Set<Month> parseMonths(String value) {
        if (value == null || value.isBlank()) {
            return Set.of(Month.values());
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(month -> !month.isBlank())
                .map(day -> Month.valueOf(day.toUpperCase()))
                .collect(Collectors.toSet());
    }
}
