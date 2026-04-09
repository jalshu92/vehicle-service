--liquibase formatted sql
--changeset jalaj:003-add-constraints

ALTER TABLE vehicles
ALTER COLUMN registration_date SET NOT NULL,
ALTER COLUMN owner_name SET NOT NULL,
ALTER COLUMN vehicle_number SET NOT NULL,
ALTER COLUMN vehicle_type SET NOT NULL,
ALTER COLUMN status SET NOT NULL,
ALTER COLUMN created_at SET NOT NULL,
ALTER COLUMN engine_number SET NOT NULL,
ADD CONSTRAINT unique_vehicle_number UNIQUE (vehicle_number);
