CREATE TABLE rod.actuator
(
    id            VARCHAR(36) PRIMARY KEY,
    actuator_name VARCHAR(100) NOT NULL,
    base_url      VARCHAR(255) NOT NULL
    );

CREATE TABLE rod.actuator_schedules
(
    id               SERIAL PRIMARY KEY,
    actuator_id      VARCHAR(36) NOT NULL,
    activation_time  TIME NOT NULL,
    duration_seconds INT NOT NULL,
    enabled          BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_actuator_schedule_actuator
    FOREIGN KEY (actuator_id) REFERENCES actuator(id) ON DELETE CASCADE
    );