package eu.wodrobina.rodos.sensor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
class SensorRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String TABLE = "sensor";

    public static final String FIND_BY_ID = "SELECT id, sensor_name, sensor_comment, public_key FROM " + TABLE + " WHERE id = ?";
    public static final String FIND_BY_PUBLIC_KEY = "SELECT id, sensor_name, sensor_comment, public_key FROM " + TABLE + " WHERE public_key = ?";
    public static final String INSERT = "INSERT INTO " + TABLE + " ( sensor_name, sensor_comment, public_key) VALUES ( ?, ?, ?)";

    public SensorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Sensor save(Sensor dto) {
        if (dto == null) {
            throw new IllegalArgumentException("dto is null");
        }

        if (dto.getId() != null) {
            throw new IllegalArgumentException("Sensor cannot be stored with ID assigned.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, dto.getSensorName());
            ps.setString(2, dto.getSensorComment());
            ps.setString(3, dto.getPublicKey());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (!keys.containsKey("ID")) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }
        UUID key = (UUID) keys.get("ID");
        return new Sensor(key, dto.getSensorName(), dto.getSensorComment(), dto.getPublicKey());
    }

    public Optional<Sensor> findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID, ROW_MAPPER, id).stream().findFirst();
    }

    public Optional<Sensor> findByPublicKey(String publicKey) {
        return jdbcTemplate.query(FIND_BY_PUBLIC_KEY, ROW_MAPPER, publicKey).stream().findFirst();
    }

    private static final RowMapper<Sensor> ROW_MAPPER = (rs, rowNum) -> new Sensor(
            rs.getObject("id", UUID.class),
            rs.getString("sensor_name"),
            rs.getString("sensor_comment"),
            rs.getString("public_key")
    );

}
