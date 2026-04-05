ALTER TABLE actuator_schedules
    ADD CONSTRAINT uq_actuator_schedules_actuator_id_activation_time
    UNIQUE (actuator_id, activation_time);