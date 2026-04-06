package com.valtech.registration_service.repository;

import com.valtech.registration_service.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Vehicle findByVehicleNumber(String vehicleNumber);
}
