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

    private long days;

    private VehicleRepository vehicleRepository;

    public VehicleService(@Value("${vehicle.delete.after.days}") long days, VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
        this.days = days;
    }

    public Vehicle findByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteOlderVehicles() {
        Instant beforeRegistrationDate = Instant.now().minus(Duration.ofDays(days));
        int rowsDeleted = vehicleRepository.deleteVehiclesRegisteredBefore(beforeRegistrationDate);
        logger.info("Deleted {} vehicles by scheduled job", rowsDeleted);
    }
}
