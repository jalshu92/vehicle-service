package com.valtech.registration_service.service;

import com.valtech.registration_service.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    @Value("${registration.topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Vehicle> kafkaTemplate;

    public void sendMessage(Vehicle vehicle) {
        kafkaTemplate.send(topicName, vehicle);
    }

}
