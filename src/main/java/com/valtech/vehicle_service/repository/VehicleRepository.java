package com.valtech.vehicle_service.repository;

import com.valtech.vehicle_service.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Vehicle findByVehicleNumber(String vehicleNumber);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from Vehicle v where v.registrationDate <= :registrationDate")
    int deleteVehiclesRegisteredBefore(@Param("registrationDate") Instant registrationDate);

}
