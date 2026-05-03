package org.example.coursework2026marina.server.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Sha256PasswordEncoderTest {
    private PasswordEncoder encoder;

    @BeforeEach
    public void setup() {
        encoder = new Sha256PasswordEncoder();
    }

    @Test
    public void testEncode() {
        String password = "test123";
        String encoded = encoder.encode(password);
        assertNotNull(encoded);
        assertNotEquals(password, encoded);
        assertEquals(64, encoded.length()); // SHA-256 produces 64 hex characters
    }

    @Test
    public void testMatches() {
        String password = "test123";
        String encoded = encoder.encode(password);
        assertTrue(encoder.matches(password, encoded));
        assertFalse(encoder.matches("wrongPassword", encoded));
    }

    @Test
    public void testSamePasswordProducesSameHash() {
        String password = "test123";
        String hash1 = encoder.encode(password);
        String hash2 = encoder.encode(password);
        assertEquals(hash1, hash2);
    }
}
