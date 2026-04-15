package com.valtech.vehicle_service.service;

import com.valtech.vehicle_service.entity.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${registration.topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Vehicle> kafkaTemplate;

    public void sendMessage(Vehicle vehicle) {
        kafkaTemplate.send(topicName, vehicle);
        LOGGER.info("Message sent to topic: {}", topicName);
    }

}
