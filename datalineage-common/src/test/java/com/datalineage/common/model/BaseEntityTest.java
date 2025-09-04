package com.datalineage.common.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * Unit tests for BaseEntity
 */
class BaseEntityTest {

    // Create a concrete implementation for testing
    private static class TestEntity extends BaseEntity {
        public TestEntity() {
            super();
        }
        
        public TestEntity(String id) {
            super(id);
        }
    }

    @Test
    void testDefaultConstructor() {
        TestEntity entity = new TestEntity();
        
        assertNotNull(entity.getCreatedAt());
        assertNull(entity.getId());
        assertNull(entity.getUpdatedAt());
        assertNull(entity.getCreatedBy());
        assertNull(entity.getUpdatedBy());
    }

    @Test
    void testConstructorWithId() {
        String testId = "test-id-123";
        TestEntity entity = new TestEntity(testId);
        
        assertEquals(testId, entity.getId());
        assertNotNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
        assertNull(entity.getCreatedBy());
        assertNull(entity.getUpdatedBy());
    }

    @Test
    void testSettersAndGetters() {
        TestEntity entity = new TestEntity();
        
        String id = "entity-123";
        LocalDateTime now = LocalDateTime.now();
        String createdBy = "user1";
        String updatedBy = "user2";
        
        entity.setId(id);
        entity.setUpdatedAt(now);
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(updatedBy);
        
        assertEquals(id, entity.getId());
        assertEquals(now, entity.getUpdatedAt());
        assertEquals(createdBy, entity.getCreatedBy());
        assertEquals(updatedBy, entity.getUpdatedBy());
    }

    @Test
    void testCreatedAtIsSetOnConstruction() {
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        TestEntity entity = new TestEntity();
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        
        assertTrue(entity.getCreatedAt().isAfter(beforeCreation));
        assertTrue(entity.getCreatedAt().isBefore(afterCreation));
    }

    @Test
    void testCreatedAtCanBeOverridden() {
        TestEntity entity = new TestEntity();
        LocalDateTime customTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        
        entity.setCreatedAt(customTime);
        
        assertEquals(customTime, entity.getCreatedAt());
    }
}