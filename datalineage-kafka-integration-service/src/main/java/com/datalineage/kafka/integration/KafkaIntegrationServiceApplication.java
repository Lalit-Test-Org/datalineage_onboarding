package com.datalineage.kafka.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for Kafka Integration Service
 */
@SpringBootApplication
@EnableDiscoveryClient
public class KafkaIntegrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaIntegrationServiceApplication.class, args);
    }
}