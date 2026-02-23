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