package org.example.coursework2026marina.common;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RequestTest {
    @Test
    public void testRequestCreation() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("test", "value");

        Request request = Request.of(Command.LOGIN, payload);

        assertNotNull(request.getRequestId());
        assertEquals(Command.LOGIN, request.getCommand());
        assertEquals(payload, request.getPayload());
        assertNotNull(request.getTimestamp());
    }

    @Test
    public void testRequestWithToken() {
        Request request = Request.of(Command.PING);
        request.withToken("token123");

        assertEquals("token123", request.getToken());
    }

    @Test
    public void testRequestPayloadCanBeEmpty() {
        Request request = Request.of(Command.PING);
        assertNotNull(request.getPayload());
        assertTrue(request.getPayload().isEmpty());
    }
}
