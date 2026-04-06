package com.valtech.registration_service.controller;

import com.valtech.registration_service.entity.Vehicle;
import com.valtech.registration_service.model.VehicleStatus;
import com.valtech.registration_service.service.KafkaProducer;
import com.valtech.registration_service.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        vehicle.setCreatedAt(Instant.now());
        vehicle.setStatus(VehicleStatus.PENDING);
        Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
        kafkaProducer.sendMessage(savedVehicle);
        return savedVehicle;
    }

}
