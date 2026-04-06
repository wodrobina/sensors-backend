package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.actuator.api.SensorReadingType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ActuatorConditionRepository {

    private final JdbcTemplate jdbcTemplate;

    public ActuatorConditionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ActuatorCondition> rowMapper = (rs, rowNum) ->
            new ActuatorCondition(
                    UUID.fromString(rs.getString("id")),
                    UUID.fromString(rs.getString("schedule_id")),
                    UUID.fromString(rs.getString("sensor_id")),
                    SensorReadingType.valueOf(rs.getString("reading_type")),
                    ComparisonOperator.valueOf(rs.getString("operator")),
                    rs.getBigDecimal("expected_value"),
                    rs.getBoolean("enabled")
            );

    public List<ActuatorCondition> findEnabledByScheduleId(ScheduleId scheduleId) {
        String sql = """
                SELECT id, schedule_id, sensor_id, reading_type, operator, expected_value, enabled
                FROM actuator_conditions
                WHERE schedule_id = ?
                  AND enabled = true
                ORDER BY id
                """;

        return jdbcTemplate.query(sql, rowMapper, scheduleId.id().toString());
    }

    public ActuatorCondition save(ActuatorCondition condition) {
        String sql = """
                INSERT INTO actuator_conditions
                    (schedule_id, sensor_id, reading_type, operator, expected_value, enabled)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, condition.getScheduleId());
            ps.setObject(2, condition.getSensorId());
            ps.setString(3, condition.getReadingType().name());
            ps.setString(4, condition.getOperator().name());
            ps.setBigDecimal(5, condition.getExpectedValue());
            ps.setBoolean(6, condition.isEnabled());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (!keys.containsKey("ID")) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }
        UUID key = (UUID) keys.get("ID");
        return new ActuatorCondition(
                key,
                condition.getScheduleId(),
                condition.getSensorId(),
                condition.getReadingType(),
                condition.getOperator(),
                condition.getExpectedValue(),
                condition.isEnabled()
        );
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM actuator_conditions WHERE id = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    public void deleteByScheduleId(UUID scheduleId) {
        String sql = "DELETE FROM actuator_conditions WHERE schedule_id = ?";
        jdbcTemplate.update(sql, scheduleId.toString());
    }
}