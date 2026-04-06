package eu.wodrobina.rodos.actuator;

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
class ActuatorRepository {

    private final JdbcTemplate jdbcTemplate;

    public ActuatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Actuator> rowMapper = (rs, rowNum) ->
            new Actuator(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("actuator_name"),
                    rs.getString("base_url")
            );

    Actuator save(String actuatorName, String baseUrl) {

        String sql = """
                INSERT INTO actuator (actuator_name, base_url)
                VALUES (?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, actuatorName);
            ps.setString(2, baseUrl);
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (!keys.containsKey("ID")) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }
        UUID key = (UUID) keys.get("ID");
        return new Actuator(key, actuatorName, baseUrl);
    }

    void deleteById(UUID id) {
        String sql = "DELETE FROM actuator WHERE id = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    Actuator findById(UUID id) {
        String sql = """
                SELECT id, actuator_name, base_url
                FROM actuator
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(sql, rowMapper, id.toString());
    }

    List<Actuator> findAll() {
        String sql = """
                SELECT id, actuator_name, base_url
                FROM actuator
                """;
        return jdbcTemplate.query(sql, rowMapper);
    }
}
