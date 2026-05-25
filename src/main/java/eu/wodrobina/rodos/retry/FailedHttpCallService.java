package eu.wodrobina.rodos.retry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FailedHttpCallService {

    private final JdbcTemplate jdbcTemplate;

    FailedHttpCallService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveIfNotExists(String url) {
        jdbcTemplate.update(
                """
                        INSERT INTO failed_http_calls (id, url, created_at, attempts)
                        VALUES (?, ?, ?, 0)
                        ON CONFLICT (url) DO NOTHING
                        """,
                UUID.randomUUID(),
                url,
                Timestamp.valueOf(LocalDateTime.now())
        );
    }

    List<FailedHttpCall> findAll() {
        return jdbcTemplate.query(
                """
                        SELECT id, url, created_at, last_attempt_at, attempts
                        FROM failed_http_calls
                        ORDER BY created_at ASC
                        """,
                (rs, rowNum) -> new FailedHttpCall(
                        rs.getObject("id", UUID.class),
                        rs.getString("url"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("last_attempt_at") != null
                                ? rs.getTimestamp("last_attempt_at").toLocalDateTime()
                                : null,
                        rs.getInt("attempts")
                )
        );
    }

    void markAttempt(UUID id) {
        jdbcTemplate.update(
                """
                        UPDATE failed_http_calls
                        SET attempts = attempts + 1,
                            last_attempt_at = ?
                        WHERE id = ?
                        """,
                Timestamp.valueOf(LocalDateTime.now()),
                id
        );
    }

    void deleteById(UUID id) {
        jdbcTemplate.update(
                "DELETE FROM failed_http_calls WHERE id = ?",
                id
        );
    }
}
