package com.valtech.vehicle_service.service;

import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle findByVehicleNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber);
    }

    public Vehicle saveVehicle(Vehicle vehicle) {
            return vehicleRepository.save(vehicle);
    }
}
