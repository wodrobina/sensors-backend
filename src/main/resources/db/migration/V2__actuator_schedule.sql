CREATE TABLE rod.actuator
(
    id            uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    actuator_name VARCHAR(100) NOT NULL,
    base_url      VARCHAR(255) NOT NULL
    );

CREATE TABLE rod.actuator_schedules
(
    id               uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    actuator_id      uuid NOT NULL,
    activation_time  TIME NOT NULL,
    duration_seconds INT NOT NULL,
    enabled          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_actuator_schedule_actuator
    FOREIGN KEY (actuator_id) REFERENCES actuator(id) ON DELETE CASCADE
    );