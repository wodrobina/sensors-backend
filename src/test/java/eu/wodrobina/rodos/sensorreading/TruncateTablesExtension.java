package eu.wodrobina.rodos.sensorreading;


import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.util.Preconditions.checkNotNull;


public abstract class TruncateTablesExtension implements AfterEachCallback {

    protected final JdbcTemplate jdbcTemplate;

    public TruncateTablesExtension(@NotNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
    }
}
