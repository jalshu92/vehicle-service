package com.valtech.vehicle_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.model.VehicleStatus;
import com.valtech.vehicle_service.service.KafkaProducer;
import com.valtech.vehicle_service.service.VehicleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(VehicleController.class)
@Import(JacksonAutoConfiguration.class)
public class VehicleControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private KafkaProducer kafkaProducer;

    @Autowired
    private ObjectMapper objectMapper;

    private Vehicle vehicle;

    @BeforeEach
    public void setup() {
        vehicle = new Vehicle();
        vehicle.setVehicleNumber("VIN0000099");
        vehicle.setRegistrationDate(Instant.now());
        vehicle.setStatus(VehicleStatus.PENDING);
        vehicle.setCreatedAt(Instant.now());
        vehicle.setEngineNumber("E00999");
        vehicle.setOwnerName("John Doe");
        vehicle.setVehicleType("Porsche");
    }


    @Test
    void testRegisterVehicle_withValidData_returnRegisteredVehicle() throws Exception {

        when(vehicleService.findByVehicleNumber(anyString())).thenReturn(null);
        when(vehicleService.saveVehicle(any(Vehicle.class))).thenReturn(vehicle);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/vehicle/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicle));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        Vehicle result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Vehicle.class);

        Assertions.assertEquals(201, mvcResult.getResponse().getStatus());
        Assertions.assertEquals(vehicle.getVehicleNumber(), result.getVehicleNumber());
        Assertions.assertEquals(vehicle.getRegistrationDate(), result.getRegistrationDate());
        Assertions.assertEquals(vehicle.getStatus(), result.getStatus());
        Assertions.assertEquals(vehicle.getCreatedAt(), result.getCreatedAt());
        Assertions.assertEquals(vehicle.getEngineNumber(), result.getEngineNumber());
        Assertions.assertEquals(vehicle.getOwnerName(), result.getOwnerName());
        verify(vehicleService).saveVehicle(any(Vehicle.class));
        verify(kafkaProducer).sendMessage(vehicle);
    }

    @Test
    void testRegisterVehicle_withInvalidVData_returnError() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleNumber("VIN000009");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/vehicle/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicle));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        Map<String, String> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);

        Assertions.assertTrue(result.containsValue("Vehicle number must be fixed length of 10"));
        Assertions.assertTrue(result.containsValue("Registration date is required"));
        Assertions.assertTrue(result.containsValue("Owner name is required"));
        Assertions.assertTrue(result.containsValue("Vehicle type is required"));
        Assertions.assertTrue(result.containsValue("Engine number is required"));
        Assertions.assertEquals(400, mvcResult.getResponse().getStatus());
    }

    @Test
    void testRegisterVehicle_withDuplicateVehicleNumber_returnError() throws Exception {
        when(vehicleService.findByVehicleNumber("VIN0000099")).thenReturn(new Vehicle());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/vehicle/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicle));
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        Map<String, String> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Map.class);

        Assertions.assertEquals(409, mvcResult.getResponse().getStatus());
        Assertions.assertTrue(result.containsValue("Vehicle already exists for vehicle number: VIN0000099"));
    }
}
