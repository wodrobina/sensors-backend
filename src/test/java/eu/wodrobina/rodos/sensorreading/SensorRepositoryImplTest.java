package eu.wodrobina.rodos.sensorreading;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class SensorRepositoryImplTest {

    @Autowired
    private SensorRepository sensorRepository;

    SensorReading sensorReading;

    @Test
    void should_save_sensor_reading() {
        givenNewSensorReading();

        assertThat(sensorRepository.save(sensorReading))
                .extracting(SensorReading::id)
                .isNotNull();
    }

    @Test
    void should_find_sensor_reading() {
        givenExistingSensorReading();

        assertThat(sensorRepository.findById(sensorReading.id()))
                .isPresent()
                .get()
                .isEqualTo(sensorReading);
    }

    @Test
    void should_find_all_sensor_readings() {
        givenExistingSensorReading(25);
        givenExistingSensorReading(24);
        givenExistingSensorReading(20);

        assertThat(sensorRepository.findAll())
                .hasSize(3);
    }

    private void givenNewSensorReading() {
        sensorReading = SensorReading.of("exampleSensor", 25, SensorUnit.CELSIUS);
    }

    private void givenExistingSensorReading() {
        givenExistingSensorReading(25);
    }

    private void givenExistingSensorReading(double reading) {
        sensorReading = sensorRepository.save(SensorReading.of("exampleSensor", reading, SensorUnit.CELSIUS));
    }
}