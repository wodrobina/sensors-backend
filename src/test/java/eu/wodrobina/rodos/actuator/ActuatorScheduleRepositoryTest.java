package eu.wodrobina.rodos.actuator;

import eu.wodrobina.rodos.TruncateTablesExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ActuatorScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ActuatorRepository actuatorRepository;

    @Autowired
    @RegisterExtension
    private TruncateTablesExtension truncateTablesExtension;

    Actuator actuator;


    @Test
    void should_save_actuator_schedule_with_days_of_week_and_months() {
        givenStoredActuator();

        Set<DayOfWeek> daysOfWeek = Set.of(
                DayOfWeek.MONDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.FRIDAY
        );

        Set<Month> months = Set.of(
                Month.JUNE,
                Month.JULY,
                Month.AUGUST
        );

        ActuatorSchedule schedule = scheduleRepository.saveSchedule(
                actuator.getActuatorId(),
                LocalTime.of(6, 30),
                daysOfWeek,
                months,
                3600,
                true
        );

        assertThat(schedule.getScheduleId()).isNotNull();
        assertThat(schedule.getActuatorId()).isEqualTo(actuator.getActuatorId());
        assertThat(schedule.getActivationTime()).isEqualTo(LocalTime.of(6, 30));
        assertThat(schedule.getDaysOfWeek()).containsExactlyInAnyOrderElementsOf(daysOfWeek);
        assertThat(schedule.getMonths()).containsExactlyInAnyOrderElementsOf(months);
        assertThat(schedule.getDurationSeconds()).isEqualTo(3600);
        assertThat(schedule.isEnabled()).isTrue();
    }

    @Test
    void should_save_actuator_schedule_with_all_days_and_all_months_when_sets_are_null() {
        givenStoredActuator();

        ActuatorSchedule schedule = scheduleRepository.saveSchedule(
                actuator.getActuatorId(),
                LocalTime.of(5, 0),
                null,
                null,
                1800,
                true
        );

        assertThat(schedule.getScheduleId()).isNotNull();
        assertThat(schedule.getActuatorId()).isEqualTo(actuator.getActuatorId());
        assertThat(schedule.getActivationTime()).isEqualTo(LocalTime.of(5, 0));
        assertThat(schedule.getDaysOfWeek()).containsExactlyInAnyOrder(DayOfWeek.values());
        assertThat(schedule.getMonths()).containsExactlyInAnyOrder(Month.values());
        assertThat(schedule.getDurationSeconds()).isEqualTo(1800);
        assertThat(schedule.isEnabled()).isTrue();
    }

    @Test
    void should_save_actuator_schedule_with_all_days_and_all_months_when_sets_are_empty() {
        givenStoredActuator();

        ActuatorSchedule schedule = scheduleRepository.saveSchedule(
                actuator.getActuatorId(),
                LocalTime.of(7, 15),
                Set.of(),
                Set.of(),
                2400,
                false
        );

        assertThat(schedule.getScheduleId()).isNotNull();
        assertThat(schedule.getActuatorId()).isEqualTo(actuator.getActuatorId());
        assertThat(schedule.getActivationTime()).isEqualTo(LocalTime.of(7, 15));
        assertThat(schedule.getDaysOfWeek()).containsExactlyInAnyOrder(DayOfWeek.values());
        assertThat(schedule.getMonths()).containsExactlyInAnyOrder(Month.values());
        assertThat(schedule.getDurationSeconds()).isEqualTo(2400);
        assertThat(schedule.isEnabled()).isFalse();
    }

    private void givenTransientActuator() {
        actuator = new Actuator("Terrace Lamp", "localhost:8080");
    }

    private void givenStoredActuator() {
        actuator = actuatorRepository.save(new Actuator("Terrace Lamp", "localhost:8080"));
    }
}