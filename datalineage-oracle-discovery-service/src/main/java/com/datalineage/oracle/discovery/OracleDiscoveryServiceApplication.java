package com.datalineage.oracle.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main application class for Oracle Discovery Service
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OracleDiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OracleDiscoveryServiceApplication.class, args);
    }
}