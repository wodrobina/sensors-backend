package eu.wodrobina.rodos.sensorreading;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
class DataSourceTestConfiguration {

    @Bean
    public TruncateTablesExtension truncateTablesRuleH2(JdbcTemplate jdbcTemplate) {
        return new TruncateTablesInH2Extension(jdbcTemplate);
    }
}
