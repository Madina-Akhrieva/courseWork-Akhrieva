package org.example.coursework2026marina.common;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private Command command;
    private String token;
    private Map<String, Object> payload;
    private Instant timestamp;

    public Request() {
    }

    public Request(Command command, Map<String, Object> payload) {
        this.requestId = UUID.randomUUID().toString();
        this.command = command;
        this.payload = payload == null ? new HashMap<>() : payload;
        this.timestamp = Instant.now();
    }

    public static Request of(Command command) {
        return new Request(command, new HashMap<>());
    }

    public static Request of(Command command, Map<String, Object> payload) {
        return new Request(command, payload);
    }

    public Request withToken(String token) {
        this.token = token;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
