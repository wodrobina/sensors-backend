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
import java.util.Optional;
import java.util.UUID;

@Repository
class ActuatorRepository {

    private final JdbcTemplate jdbcTemplate;

    public ActuatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    Actuator save(Actuator dto) {

        if (dto == null) {
            throw new IllegalArgumentException("dto is null");
        }

        if (dto.getActuatorId() != null) {
            throw new IllegalArgumentException("Actuator cannot be stored with ID assigned.");
        }

        String sql = """
                INSERT INTO actuator (actuator_name, base_url)
                VALUES (?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getActuatorName());
            ps.setString(2, dto.getBaseUrl());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (!keys.containsKey("ID")) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }
        UUID key = (UUID) keys.get("ID");
        dto.setId(new ActuatorId(key));
        return dto;
    }

    void deleteById(ActuatorId id) {
        String sql = "DELETE FROM actuator WHERE id = ?";
        jdbcTemplate.update(sql, id.id().toString());
    }

    Optional<Actuator> findById(ActuatorId id) {
        String sql = """
                SELECT id, actuator_name, base_url
                FROM actuator
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, rowMapper, id.id().toString()).stream().findFirst();
    }

    List<Actuator> findAll() {
        String sql = """
                SELECT id, actuator_name, base_url
                FROM actuator
                """;
        return jdbcTemplate.query(sql, rowMapper);
    }

    private static final RowMapper<Actuator> rowMapper = (rs, rowNum) ->
            new Actuator(
                    new ActuatorId(UUID.fromString(rs.getString("id"))),
                    rs.getString("actuator_name"),
                    rs.getString("base_url")
            );
}
