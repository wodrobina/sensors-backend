package eu.wodrobina.rodos.sensorreading;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
class SensorReadingRepositoryImpl implements SensorReadingRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String TABLE = "sensor_reading";

    public static final String SAVE = "INSERT INTO " + TABLE + " (sensor_id, reading, unit, created_at) VALUES (?, ?, ?, ?)";
    public static final String FIND_BY_SENSOR_ID = "SELECT id, sensor_id, reading, unit, created_at FROM " + TABLE + " WHERE sensor_id = ?";
    public static final String FIND_BY_SENSOR_ID_AND_TIME_RANGE =
            "SELECT id, sensor_id, reading, unit " +
                    "FROM " + TABLE + " " +
                    "WHERE sensor_id = ? " +
                    "AND created_at >= ? " +
                    "AND created_at <= ?";

    public SensorReadingRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SensorReading> findAllBySensorId(UUID sensorId) {
        return jdbcTemplate.query(FIND_BY_SENSOR_ID, ROW_MAPPER, sensorId);
    }

    @Override
    public List<SensorReading> findAllBySensorId(UUID sensorId, Instant from, Instant to) {
        return jdbcTemplate.query(
                FIND_BY_SENSOR_ID_AND_TIME_RANGE,
                ROW_MAPPER,
                sensorId,
                from,
                to
        );
    }

    @Override
    public SensorReading save(SensorReading dto) {
        if (dto == null) {
            throw new IllegalArgumentException("dto is null");
        }

        if (dto.id() != null) {
            throw new IllegalArgumentException("SensorReading cannot be stored with ID assigned.");
        }

        return insert(dto);
    }

    private SensorReading insert(SensorReading dto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, dto.sensorId());
            ps.setBigDecimal(2, dto.reading());
            ps.setString(3, dto.unit());
            ps.setTimestamp(4, Timestamp.from(dto.createdAt()));
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (!keys.containsKey("ID")) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }
        Number key = (Number) keys.get("ID");
        long newId = key.longValue();
        return new SensorReading(newId, dto.sensorId(), dto.reading(), dto.unit(), Instant.now());
    }

    private static final RowMapper<SensorReading> ROW_MAPPER = (rs, rowNum) -> new SensorReading(
            rs.getLong("id"),
            rs.getObject("sensor_id", UUID.class),
            rs.getBigDecimal("reading"),
            rs.getString("unit"),
            rs.getObject("created_at", java.time.OffsetDateTime.class).toInstant()
    );

}
