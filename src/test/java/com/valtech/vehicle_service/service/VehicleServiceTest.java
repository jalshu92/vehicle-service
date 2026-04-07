package com.valtech.vehicle_service.service;

import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.model.VehicleStatus;
import com.valtech.vehicle_service.repository.VehicleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void testFindByVehicleNumber() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("VIN0000099");
        vehicle.setId(9999L);
        vehicle.setRegistrationDate(Instant.now());
        vehicle.setStatus(VehicleStatus.PENDING);
        vehicle.setCreatedAt(Instant.now());
        vehicle.setEngineNumber("E00999");
        vehicle.setOwnerName("John Doe");

        Mockito.when(vehicleRepository.findByVehicleNumber(vehicle.getVehicleNumber())).thenReturn(vehicle);
        Vehicle vehicle1 = vehicleService.findByVehicleNumber(vehicle.getVehicleNumber());
        Assertions.assertEquals(vehicle, vehicle1);
        Mockito.verify(vehicleRepository).findByVehicleNumber(vehicle.getVehicleNumber());
    }

    @Test
    void testSaveVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("VIN0000099");
        vehicle.setId(9999L);
        vehicle.setRegistrationDate(Instant.now());
        vehicle.setStatus(VehicleStatus.PENDING);
        vehicle.setCreatedAt(Instant.now());
        vehicle.setEngineNumber("E00999");
        vehicle.setOwnerName("John Doe");

        Mockito.when(vehicleRepository.save(Mockito.any(Vehicle.class))).thenReturn(vehicle);
        Vehicle vehicle1 = vehicleService.saveVehicle(new Vehicle());
        Assertions.assertEquals(vehicle, vehicle1);
        Mockito.verify(vehicleRepository).save(Mockito.any(Vehicle.class));
    }
}
