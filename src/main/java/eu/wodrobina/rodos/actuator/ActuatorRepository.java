package eu.wodrobina.rodos.actuator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ActuatorRepository {

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

    public Actuator save(String actuatorName, String baseUrl) {
        UUID id = UUID.randomUUID();

        String sql = """
                INSERT INTO actuator (id, actuator_name, base_url)
                VALUES (?, ?, ?)
                """;

        jdbcTemplate.update(sql, id.toString(), actuatorName, baseUrl);

        return new Actuator(id, actuatorName, baseUrl);
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM actuator WHERE id = ?";
        jdbcTemplate.update(sql, id.toString());
    }

    public Actuator findById(UUID id) {
        String sql = """
                SELECT id, actuator_name, base_url
                FROM actuator
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(sql, rowMapper, id.toString());
    }

    public List<Actuator> findAll() {
        String sql = """
                SELECT id, actuator_name, base_url
                FROM actuator
                """;
        return jdbcTemplate.query(sql, rowMapper);
    }
}
