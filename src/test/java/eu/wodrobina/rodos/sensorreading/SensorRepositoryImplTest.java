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
    void should_save_new_sensor_reading() {
        givenNewSensorReading();

        assertThat(sensorRepository.save(sensorReading))
                .extracting(SensorReading::id)
                .isNotNull();
    }

    @Test
    void should_find_sensor_readings_by_sensor_name() {
        givenExistingSensorReading(25);
        givenExistingSensorReading("differentSensor", 20);
        givenExistingSensorReading("differentSensor", 21);

        assertThat(sensorRepository.findAllBySensorName("differentSensor"))
                .allMatch(sr -> sr.sensorName().equals("differentSensor"));
    }

    private void givenNewSensorReading() {
        sensorReading = SensorReading.of("exampleSensor", 25, SensorUnit.CELSIUS);
    }

    private void givenExistingSensorReading() {
        givenExistingSensorReading(25);
    }

    private void givenExistingSensorReading(double reading) {
        givenExistingSensorReading("exampleSensor", reading);
    }

    private void givenExistingSensorReading(String sensorName, double reading) {
        sensorReading = sensorRepository.save(SensorReading.of(sensorName, reading, SensorUnit.CELSIUS));
    }
}