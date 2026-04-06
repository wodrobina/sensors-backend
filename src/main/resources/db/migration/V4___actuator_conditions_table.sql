CREATE TABLE actuator_conditions
(
    id             uuid                    DEFAULT gen_random_uuid() PRIMARY KEY,
    schedule_id    uuid           NOT NULL,
    sensor_id      uuid           NOT NULL,
    reading_type   VARCHAR(50)    NOT NULL,
    operator       VARCHAR(30)    NOT NULL,
    expected_value NUMERIC(10, 2) NOT NULL,
    enabled        BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_actuator_conditions_schedule
        FOREIGN KEY (schedule_id) REFERENCES actuator_schedules (id) ON DELETE CASCADE
);