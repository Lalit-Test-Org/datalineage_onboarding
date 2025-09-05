package com.datalineage.oracle.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for OracleDiscoveryServiceApplication
 */
@SpringBootTest
@TestPropertySource(properties = {
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "datalineage.encryption.key=test-encryption-key-32-chars"
})
class OracleDiscoveryServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring Boot application context loads successfully
        // The @SpringBootTest annotation will start the application context
    }
}