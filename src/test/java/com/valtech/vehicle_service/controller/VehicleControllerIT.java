package com.valtech.vehicle_service.controller;

import com.valtech.vehicle_service.entity.Vehicle;
import com.valtech.vehicle_service.model.VehicleStatus;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class VehicleControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    @ServiceConnection
    private static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18.3");

    @Container
    @ServiceConnection
    private static KafkaContainer kafka = new KafkaContainer("apache/kafka-native:4.2.0");

    @Test
    void testRegisterVehicle_withValidData_returnRegisteredVehicle() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("vehicleNumber", "VIN0000999");
        requestBody.put("engineNumber", "E00999");
        requestBody.put("ownerName", "John Doed");
        requestBody.put("vehicleType", "Audi");
        requestBody.put("registrationDate", Instant.now());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<Vehicle> response = restTemplate.postForEntity("/vehicle/register", request, Vehicle.class);
        Vehicle vehicle = response.getBody();

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(requestBody.getString("vehicleNumber"), vehicle.getVehicleNumber());
        Assertions.assertEquals(requestBody.getString("engineNumber"), vehicle.getEngineNumber());
        Assertions.assertEquals(requestBody.getString("ownerName"), vehicle.getOwnerName());
        Assertions.assertEquals(requestBody.getString("vehicleType"), vehicle.getVehicleType());
        Assertions.assertEquals((Instant) requestBody.get("registrationDate"), vehicle.getRegistrationDate());
        Assertions.assertEquals(VehicleStatus.PENDING, vehicle.getStatus());
        Assertions.assertNotNull(vehicle.getCreatedAt());
        Assertions.assertNotNull(vehicle.getId());

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                kafka.getBootstrapServers(), "test-group", "true");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, Object> consumer = new DefaultKafkaConsumerFactory<>(
                consumerProps, new StringDeserializer(), new JsonDeserializer<>()).createConsumer();
        consumer.subscribe(Collections.singletonList("vehicle-registered"));

        ConsumerRecord<String, Object> record = KafkaTestUtils.getSingleRecord(consumer, "vehicle-registered");
        Vehicle consumedVehicle = (Vehicle) record.value();
        Assertions.assertEquals(requestBody.getString("vehicleNumber"), consumedVehicle.getVehicleNumber());
        Assertions.assertEquals(requestBody.getString("engineNumber"), consumedVehicle.getEngineNumber());
        Assertions.assertEquals(requestBody.getString("ownerName"), consumedVehicle.getOwnerName());
        Assertions.assertEquals(requestBody.getString("vehicleType"), consumedVehicle.getVehicleType());
        Assertions.assertEquals((Instant) requestBody.get("registrationDate"), consumedVehicle.getRegistrationDate());
        Assertions.assertEquals(VehicleStatus.PENDING, consumedVehicle.getStatus());
        Assertions.assertNotNull(consumedVehicle.getCreatedAt());
        Assertions.assertNotNull(consumedVehicle.getId());
    }
}
