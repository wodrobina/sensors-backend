package eu.wodrobina.rodos.sensorreading;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class SensorRepositoryImpl implements SensorRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String TABLE = "sensors";
    public static final String SAVE = "INSERT INTO " + TABLE + " (sensor_name, reading, unit) VALUES (?, ?, ?)";
    public static final String FIND_BY_ID = "SELECT id, sensor_name, reading, unit FROM " + TABLE + " WHERE id = ?";
    public static final String FIND_ALL = "SELECT id, sensor_name, reading, unit FROM " + TABLE + " ORDER BY id";

    public SensorRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SensorReading> findAll() {
        return jdbcTemplate.query(FIND_ALL, ROW_MAPPER);
    }

    @Override
    public Optional<SensorReading> findById(long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID, ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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
            ps.setString(1, dto.sensorName());
            ps.setBigDecimal(2, dto.reading());
            ps.setString(3, dto.unit());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Insert succeeded but no generated key returned");
        }

        long newId = key.longValue();
        return new SensorReading(newId, dto.sensorName(), dto.reading(), dto.unit());
    }

    private static final RowMapper<SensorReading> ROW_MAPPER = (rs, rowNum) -> new SensorReading(
            rs.getLong("id"),
            rs.getString("sensor_name"),
            rs.getBigDecimal("reading"),
            rs.getString("unit")
    );

}
