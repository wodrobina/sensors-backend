package eu.wodrobina.rodos.sensor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SensorConfiguration {

    @Bean
    SensorRepository sensorRepository(JdbcTemplate jdbcTemplate){
        return new SensorRepository(jdbcTemplate);
    }

    @Bean
    SensorService sensorService(SensorRepository sensorRepository){
        return new SensorService(sensorRepository);
    }
}
