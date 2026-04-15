package com.valtech.vehicle_service.scheduler;

import com.valtech.vehicle_service.service.VehicleService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = "vehicle.delete.job.cron.expression=*/1 * * * * *")
public class DeleteVehiclesSchedulerTest {

    @MockitoSpyBean
    private DeleteVehiclesScheduler deleteVehiclesScheduler;

    @MockitoBean
    private VehicleService vehicleService;

    @Test
    void jobRuns() {
        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                    verify(deleteVehiclesScheduler, Mockito.atLeastOnce()).deleteOlderVehicles()
        );
    }
}
