alter table rod.sensor_reading
    add column sensor_id uuid;

update rod.sensor_reading sr
set sensor_id = s.id
    from rod.sensor s
where sr.sensor_name = s.sensor_name
  and sr.sensor_id is null;

alter table rod.sensor_reading
    alter column sensor_id set not null;

alter table rod.sensor_reading
    add constraint fk_sensor_reading_sensor
        foreign key (sensor_id)
            references rod.sensor(id)
            on delete cascade;

create index idx_sensor_reading_sensor_id
    on rod.sensor_reading(sensor_id);

drop index if exists rod.idx_sensor_reading_sensor_name;

alter table rod.sensor_reading
drop column sensor_name;