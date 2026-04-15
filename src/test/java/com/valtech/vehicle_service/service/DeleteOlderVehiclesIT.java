package com.valtech.vehicle_service.service;

import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.model.VehicleStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = "vehicle.delete.after.days=7")
public class DeleteOlderVehiclesIT {

    @Autowired
    private VehicleService vehicleService;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.3");

    @Test
    void testDeleteVehicles() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("VIN0000001");
        vehicle.setVehicleType("Audi");
        vehicle.setOwnerName("John");
        vehicle.setRegistrationDate(Instant.now().minus(Duration.ofDays(7)).minus(Duration.ofMinutes(5)));
        vehicle.setEngineNumber("E0001");
        vehicle.setStatus(VehicleStatus.PENDING);
        vehicle.setCreatedAt(Instant.now());
        vehicle = vehicleService.saveVehicle(vehicle);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleNumber("VIN0000002");
        vehicle2.setVehicleType("VW");
        vehicle2.setOwnerName("Jalaj");
        vehicle2.setRegistrationDate(Instant.now().minus(Duration.ofDays(7)).plus(Duration.ofMinutes(5)));
        vehicle2.setEngineNumber("E0002");
        vehicle2.setStatus(VehicleStatus.PENDING);
        vehicle2.setCreatedAt(Instant.now());
        vehicle2 = vehicleService.saveVehicle(vehicle2);

        Assertions.assertNotNull(vehicleService.findByVehicleNumber(vehicle.getVehicleNumber()));
        Assertions.assertNotNull(vehicleService.findByVehicleNumber(vehicle2.getVehicleNumber()));

        vehicleService.deleteOlderVehicles();
        Assertions.assertNull(vehicleService.findByVehicleNumber(vehicle.getVehicleNumber()));
        Assertions.assertNotNull(vehicleService.findByVehicleNumber(vehicle2.getVehicleNumber()));

    }
}
