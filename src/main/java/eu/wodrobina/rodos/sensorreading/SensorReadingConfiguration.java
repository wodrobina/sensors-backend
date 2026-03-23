package eu.wodrobina.rodos.sensorreading;

import eu.wodrobina.rodos.sensor.SensorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
class SensorReadingConfiguration {

    @Bean
    SensorReadingRepository sensorReadingRepository(JdbcTemplate jdbcTemplate) {
        return new SensorReadingRepository(jdbcTemplate);
    }

    @Bean
    SensorReadingService sensorReadingService(SensorReadingRepository sensorReadingRepository, SensorService sensorService) {
        return new SensorReadingService(sensorReadingRepository, sensorService);
    }
}
