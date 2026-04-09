package com.valtech.vehicle_service.service;

import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Value("${vehicle.delete.after.days}")
    private long days;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle findByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteOlderVehicles() {
        Instant beforeRegistrationDate = Instant.now().minus(Duration.ofDays(days));
        Instant beforeCreatedAt = Instant.now().minus(Duration.ofMinutes(30));
        int rowsDeleted = vehicleRepository.deleteVehiclesBeforeCreatedAtAndBeforeRegistrationDateTime(beforeCreatedAt, beforeRegistrationDate);
        logger.info("Deleted {} vehicles by scheduled job", rowsDeleted);
    }
}
