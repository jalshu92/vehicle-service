package com.valtech.registration_service.service;

import com.valtech.registration_service.entity.Vehicle;
import com.valtech.registration_service.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle findByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
            return vehicleRepository.save(vehicle);
    }
}
