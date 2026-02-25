package eu.wodrobina.rodos.sensorreading;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SensorReadingRepositoryImplTest {

    @Autowired
    private SensorReadingRepository sensorRepository;

    @Autowired
    @RegisterExtension
    private TruncateTablesExtension truncateTablesExtension;

    SensorReading sensorReading;

    UUID currentSensorId = UUID.randomUUID();
    UUID anotherSensorId = UUID.randomUUID();

    @Test
    void should_save_new_sensor_reading() {
        givenNewSensorReading();

        assertThat(sensorRepository.save(sensorReading))
                .extracting(SensorReading::id)
                .isNotNull();
    }

    @Test
    void should_find_sensor_readings_by_sensor_name() {
        givenExistingSensorReading(25);

        givenExistingSensorReading(anotherSensorId, 20);
        givenExistingSensorReading(anotherSensorId, 21);

        assertThat(sensorRepository.findAllBySensorId(anotherSensorId))
                .allMatch(sr -> sr.sensorId().equals(anotherSensorId));
    }

    private void givenNewSensorReading() {
        sensorReading = SensorReading.of(currentSensorId, 25, SensorUnit.CELSIUS);
    }

    private void givenExistingSensorReading() {
        givenExistingSensorReading(25);
    }

    private void givenExistingSensorReading(double reading) {
        givenExistingSensorReading(currentSensorId, reading);
    }

    private void givenExistingSensorReading(UUID sensorId, double reading) {
        sensorReading = sensorRepository.save(SensorReading.of(sensorId, reading, SensorUnit.CELSIUS));
    }
}