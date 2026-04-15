package com.valtech.vehicle_service.repository;

import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.model.VehicleStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;

@DataJpaTest
@Testcontainers
public class VehicleRepositoryTest {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.3");

//    @Autowired
//    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void testFindByVehicleNumber_WhenVehicleNumberExists() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("VIN0000001");
        vehicle.setVehicleType("Audi");
        vehicle.setOwnerName("John");
        vehicle.setRegistrationDate(Instant.now());
        vehicle.setEngineNumber("E0001");
        vehicle.setStatus(VehicleStatus.PENDING);
        vehicle.setCreatedAt(Instant.now());
        vehicleRepository.save(vehicle);

        Vehicle result = vehicleRepository.findByVehicleNumber("VIN0000001");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(vehicle.getVehicleNumber(), result.getVehicleNumber());
        Assertions.assertEquals(vehicle.getVehicleType(), result.getVehicleType());
        Assertions.assertEquals(vehicle.getOwnerName(), result.getOwnerName());
        Assertions.assertEquals(vehicle.getRegistrationDate(), result.getRegistrationDate());
        Assertions.assertEquals(vehicle.getEngineNumber(), result.getEngineNumber());
        Assertions.assertEquals(vehicle.getStatus(), result.getStatus());
        Assertions.assertEquals(vehicle.getCreatedAt(), result.getCreatedAt());
    }

    @Test
    void testFindByVehicleNumber_WhenVehicleNumberDoesNotExist() {
        Vehicle result = vehicleRepository.findByVehicleNumber("VIN0000002");
        Assertions.assertNull(result);
    }

    @Test
    void testDeleteVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("VIN0000001");
        vehicle.setVehicleType("Audi");
        vehicle.setOwnerName("John");
        vehicle.setRegistrationDate(Instant.now().minus(Duration.ofDays(2)));
        vehicle.setEngineNumber("E0001");
        vehicle.setStatus(VehicleStatus.PENDING);
        vehicle.setCreatedAt(Instant.now());
        vehicle = vehicleRepository.save(vehicle);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setVehicleNumber("VIN0000002");
        vehicle2.setVehicleType("VW");
        vehicle2.setOwnerName("Jalaj");
        vehicle2.setRegistrationDate(vehicle.getRegistrationDate().plus(Duration.ofMinutes(2)));
        vehicle2.setEngineNumber("E0002");
        vehicle2.setStatus(VehicleStatus.PENDING);
        vehicle2.setCreatedAt(Instant.now());
        vehicle2 = vehicleRepository.save(vehicle2);

        Assertions.assertEquals(2, vehicleRepository.findAll().size());
        Assertions.assertNotNull(vehicleRepository.findById(vehicle.getId()).orElse(null));
        Assertions.assertNotNull(vehicleRepository.findById(vehicle2.getId()).orElse(null));

        int rowsDeleted = vehicleRepository.deleteVehiclesRegisteredBefore(Instant.now().minus(Duration.ofDays(2)));

        Assertions.assertEquals(1, vehicleRepository.findAll().size());
        Assertions.assertEquals(1, rowsDeleted);
        Assertions.assertNull(vehicleRepository.findById(vehicle.getId()).orElse(null));
        Assertions.assertNotNull(vehicleRepository.findById(vehicle2.getId()).orElse(null));
    }

}
