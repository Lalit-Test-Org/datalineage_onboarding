package com.datalineage.common.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiResponse
 */
class ApiResponseTest {

    @Test
    void testDefaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        
        assertFalse(response.isSuccess()); // default boolean value is false
        assertNull(response.getMessage());
        assertNull(response.getData());
        assertNull(response.getErrorCode());
    }

    @Test
    void testConstructorWithSuccessAndMessage() {
        boolean success = true;
        String message = "Operation completed";
        
        ApiResponse<String> response = new ApiResponse<>(success, message);
        
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertNull(response.getData());
        assertNull(response.getErrorCode());
    }

    @Test
    void testConstructorWithSuccessMessageAndData() {
        boolean success = true;
        String message = "Data retrieved";
        String data = "test-data";
        
        ApiResponse<String> response = new ApiResponse<>(success, message, data);
        
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getErrorCode());
    }

    @Test
    void testSuccessFactoryMethod() {
        String data = "test-data";
        
        ApiResponse<String> response = ApiResponse.success(data);
        
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getErrorCode());
    }

    @Test
    void testSuccessFactoryMethodWithMessage() {
        String message = "Custom success message";
        String data = "test-data";
        
        ApiResponse<String> response = ApiResponse.success(message, data);
        
        assertTrue(response.isSuccess());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
        assertNull(response.getErrorCode());
    }

    @Test
    void testErrorFactoryMethod() {
        String errorMessage = "Something went wrong";
        
        ApiResponse<String> response = ApiResponse.error(errorMessage);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertNull(response.getData());
        assertNull(response.getErrorCode());
    }

    @Test
    void testErrorFactoryMethodWithErrorCode() {
        String errorMessage = "Validation failed";
        String errorCode = "VALIDATION_ERROR";
        
        ApiResponse<String> response = ApiResponse.error(errorMessage, errorCode);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getMessage());
        assertEquals(errorCode, response.getErrorCode());
        assertNull(response.getData());
    }

    @Test
    void testSettersAndGetters() {
        ApiResponse<Integer> response = new ApiResponse<>();
        
        response.setSuccess(true);
        response.setMessage("Test message");
        response.setData(42);
        response.setErrorCode("TEST_CODE");
        
        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertEquals(Integer.valueOf(42), response.getData());
        assertEquals("TEST_CODE", response.getErrorCode());
    }

    @Test
    void testGenericTypeSupport() {
        // Test with different generic types
        ApiResponse<Integer> intResponse = ApiResponse.success(123);
        ApiResponse<Boolean> boolResponse = ApiResponse.success(true);
        ApiResponse<Object> objResponse = ApiResponse.error("Error");
        
        assertEquals(Integer.valueOf(123), intResponse.getData());
        assertEquals(Boolean.TRUE, boolResponse.getData());
        assertNull(objResponse.getData());
    }
}