package eu.wodrobina.rodos.actuator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                                         int durationSeconds,
                                         boolean enabled) {
        String sql = """
                INSERT INTO actuator_schedules (actuator_id, activation_time, duration_seconds, enabled)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, actuatorId.id());
            ps.setTime(2, Time.valueOf(activationTime));
            ps.setInt(3, durationSeconds);
            ps.setBoolean(4, enabled);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (!keys.containsKey("ID")) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }
        UUID key = (UUID) keys.get("ID");

        return new ActuatorSchedule(
                new ScheduleId(key),
                actuatorId,
                activationTime,
                durationSeconds,
                enabled
        );
    }

    public List<ActuatorSchedule> findSchedulesActiveAt(LocalTime now) {

        String sql = """
                
                SELECT id, actuator_id, activation_time, duration_seconds, enabled 
                FROM actuator_schedules 
                WHERE enabled = true       
                  AND activation_time <= ?::time       
                  AND (activation_time + (duration_seconds * interval '1 second')) > ?::time 
                ORDER BY activation_time 
                """;

        return jdbcTemplate.query(sql, rowMapper, Time.valueOf(now), Time.valueOf(now));

    }

    public void deleteSchedule(Long scheduleId) {
        String sql = "DELETE FROM actuator_schedules WHERE id = ?";
        jdbcTemplate.update(sql, scheduleId);
    }

    public void deleteByActuatorId(UUID actuator, UUID actuatorId) {
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
}
