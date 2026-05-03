package org.example.coursework2026marina.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseTest {
    @Test
    public void testSuccessResponse() {
        Response response = Response.ok("Test message");

        assertTrue(response.isSuccess());
        assertEquals("Test message", response.getMessage());
        assertNull(response.getErrorCode());
        assertNotNull(response.getData());
    }

    @Test
    public void testFailureResponse() {
        Response response = Response.fail("Test error", "ERROR_CODE");

        assertFalse(response.isSuccess());
        assertEquals("Test error", response.getMessage());
        assertEquals("ERROR_CODE", response.getErrorCode());
        assertNotNull(response.getData());
    }

    @Test
    public void testResponseWithData() {
        Response response = Response.ok("Success")
                .withData("key1", "value1")
                .withData("key2", 42);

        assertTrue(response.isSuccess());
        assertEquals("value1", response.getData().get("key1"));
        assertEquals(42, response.getData().get("key2"));
    }
}
