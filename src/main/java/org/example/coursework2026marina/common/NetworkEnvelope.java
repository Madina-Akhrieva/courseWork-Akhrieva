package org.example.coursework2026marina.common;

public class NetworkEnvelope {
    private String payload;
    private long checksum;

    public NetworkEnvelope() {
    }

    public NetworkEnvelope(String payload, long checksum) {
        this.payload = payload;
        this.checksum = checksum;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public long getChecksum() {
        return checksum;
    }

    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }
}
