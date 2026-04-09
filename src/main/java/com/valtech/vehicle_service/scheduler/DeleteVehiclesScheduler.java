package com.valtech.vehicle_service.scheduler;

import com.valtech.vehicle_service.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class DeleteVehiclesScheduler {

    @Autowired
    private VehicleService vehicleService;

    @Scheduled(cron = "${vehicle.delete.job.cron.expression}")
    public void deleteOlderVehicles() {
        vehicleService.deleteOlderVehicles();
    }
}
