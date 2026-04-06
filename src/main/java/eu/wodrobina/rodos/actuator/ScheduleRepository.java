package eu.wodrobina.rodos.actuator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    public ScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ActuatorSchedule> rowMapper = (rs, rowNum) ->
            new ActuatorSchedule(
                    rs.getLong("id"),
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
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, actuatorId.id().toString());
            ps.setTime(2, Time.valueOf(activationTime));
            ps.setInt(3, durationSeconds);
            ps.setBoolean(4, enabled);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }

        return new ActuatorSchedule(
                key.longValue(),
                actuatorId,
                activationTime,
                durationSeconds,
                enabled
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
}