package org.example.coursework2026marina.common;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private String errorCode;
    private Map<String, Object> data;
    private Instant timestamp;

    public Response() {
    }

    public static Response ok(String message) {
        Response response = new Response();
        response.success = true;
        response.message = message;
        response.data = new HashMap<>();
        response.timestamp = Instant.now();
        return response;
    }

    public static Response fail(String message, String errorCode) {
        Response response = new Response();
        response.success = false;
        response.message = message;
        response.errorCode = errorCode;
        response.data = new HashMap<>();
        response.timestamp = Instant.now();
        return response;
    }

    public Response withData(String key, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
