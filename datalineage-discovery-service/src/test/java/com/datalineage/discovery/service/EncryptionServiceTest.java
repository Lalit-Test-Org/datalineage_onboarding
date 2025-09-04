package com.datalineage.discovery.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EncryptionService
 */
class EncryptionServiceTest {
    
    private EncryptionService encryptionService;
    
    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        // Set the encryption key manually for testing using setter
        encryptionService.setEncryptionKey("testEncryptionKey123456");
    }
    
    @Test
    void testEncryptAndDecrypt() {
        String originalText = "sensitivePassword123";
        
        String encrypted = encryptionService.encrypt(originalText);
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);
        
        String decrypted = encryptionService.decrypt(encrypted);
        assertEquals(originalText, decrypted);
    }
    
    @Test
    void testEncryptNull() {
        String encrypted = encryptionService.encrypt(null);
        assertNull(encrypted);
    }
    
    @Test
    void testEncryptEmpty() {
        String encrypted = encryptionService.encrypt("");
        assertNull(encrypted);
    }
    
    @Test
    void testDecryptNull() {
        String decrypted = encryptionService.decrypt(null);
        assertNull(decrypted);
    }
    
    @Test
    void testDecryptEmpty() {
        String decrypted = encryptionService.decrypt("");
        assertNull(decrypted);
    }
    
    @Test
    void testEncryptLongText() {
        String longText = "This is a very long password that contains many characters and should still be encrypted and decrypted properly even though it's quite long";
        
        String encrypted = encryptionService.encrypt(longText);
        assertNotNull(encrypted);
        assertNotEquals(longText, encrypted);
        
        String decrypted = encryptionService.decrypt(encrypted);
        assertEquals(longText, decrypted);
    }
    
    @Test
    void testGenerateRandomKey() {
        String key = encryptionService.generateRandomKey();
        assertNotNull(key);
        assertTrue(key.length() > 0);
    }
}