--liquibase formatted sql
--changeset jalaj:002-alter-vehicles

ALTER TABLE vehicles
ADD COLUMN engine_number VARCHAR(30),
ADD COLUMN registration_date TIMESTAMP;