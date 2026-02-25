create schema if not exists rod;

create table rod.sensor_reading
(
    id          bigserial primary key,
    sensor_name varchar(255)   not null,
    reading     numeric(19, 2) not null,
    unit        varchar(32)    not null,
    created_at  timestamptz    not null
);

create index idx_sensor_reading_sensor_name
    on rod.sensor_reading (sensor_name);

CREATE TABLE sensor (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    sensor_name VARCHAR(255) NOT NULL,
    sensor_comment TEXT,
    public_key TEXT UNIQUE NOT NULL
);

CREATE INDEX sensor_public_key_idx ON sensor(public_key);