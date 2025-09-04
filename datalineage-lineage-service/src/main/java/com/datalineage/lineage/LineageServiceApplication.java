package com.datalineage.lineage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for Data Lineage Tracking Service
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LineageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LineageServiceApplication.class, args);
    }
}