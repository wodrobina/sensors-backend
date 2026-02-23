package eu.wodrobina.rodos.sensorreading;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class TruncateTablesInH2Extension extends TruncateTablesExtension {

    public TruncateTablesInH2Extension(@NotNull JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        truncateTable();
    }

    private void truncateTable() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.query("SHOW TABLES", (rs, rowNum) -> rs.getString(1))
                .forEach(table -> jdbcTemplate.execute("TRUNCATE TABLE `" + table + "`"));
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
