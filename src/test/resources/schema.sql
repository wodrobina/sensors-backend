CREATE TABLE sensors
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_name VARCHAR(255)   NOT NULL,
    reading     NUMERIC(19, 6) NOT NULL,
    unit        VARCHAR(32)    NOT NULL
);