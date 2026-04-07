package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.TruncateTablesExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@Transactional
class ActuatorRepositoryTest {

    @Autowired
    private ActuatorRepository actuatorRepository;

    @Autowired
    @RegisterExtension
    private TruncateTablesExtension truncateTablesExtension;

    Actuator actuator;

    @Test
    void should_save_new_actuator() {
        givenTransientActuator();

        assertThat(actuatorRepository.save(actuator))
                .extracting(Actuator::getActuatorId)
                .isNotNull();
    }

    @Test
    void should_find_actuator() {
        givenStoredActuator();

        assertThat(actuatorRepository.findById(actuator.getActuatorId()))
                .isNotEmpty();
    }

    @Test
    void should_find_all_actuators() {
        givenStoredActuator();
        givenStoredActuator();

        assertThat(actuatorRepository.findAll())
                .hasSize(2);
    }

    @Test
    void should_delete_actuator() {
        givenStoredActuator();

        assertThatNoException().isThrownBy(() -> actuatorRepository.deleteById(actuator.getActuatorId()));
        assertThat(actuatorRepository.findById(actuator.getActuatorId()))
                .isEmpty();
    }

//    @Test
//    void should_find_sensor_readings_by_sensor_name() {
//        givenExistingSensorReading(25);
//
//        givenExistingSensorReading(anotherSensorId, 20);
//        givenExistingSensorReading(anotherSensorId, 21);
//
//        assertThat(actuatorRepository.findAllBySensorId(anotherSensorId))
//                .allMatch(sr -> sr.sensorId().equals(anotherSensorId));
//    }

    private void givenTransientActuator() {
        actuator = new Actuator("Terrace Lamp", "localhost:8080");
    }

    private void givenStoredActuator() {
        actuator = actuatorRepository.save(new Actuator("Terrace Lamp", "localhost:8080"));
    }

}