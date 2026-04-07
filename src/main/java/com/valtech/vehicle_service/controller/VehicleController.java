package com.valtech.vehicle_service.controller;

import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.model.VehicleStatus;
import com.valtech.vehicle_service.service.KafkaProducer;
import com.valtech.vehicle_service.service.VehicleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private KafkaProducer kafkaProducer;

    @PostMapping("/register")
    public ResponseEntity<?> createVehicle(@RequestBody @Valid Vehicle vehicle) {
        Vehicle existingVehicle = vehicleService.findByVehicleNumber(vehicle.getVehicleNumber());
        if (existingVehicle != null) {
            logger.error("Vehicle already exists for vehicle number: " + vehicle.getVehicleNumber());
            Map<String, String> error = Map.of("vehicleNumber", "Vehicle already exists for vehicle number: " + vehicle.getVehicleNumber());
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
        vehicle.setCreatedAt(Instant.now());
        vehicle.setStatus(VehicleStatus.PENDING);
        Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
        kafkaProducer.sendMessage(savedVehicle);
        return new ResponseEntity<>(savedVehicle, HttpStatus.CREATED);
    }

}
