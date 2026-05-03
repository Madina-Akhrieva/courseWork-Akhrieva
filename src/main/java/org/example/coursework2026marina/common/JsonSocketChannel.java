package org.example.coursework2026marina.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class JsonSocketChannel {
    private final ObjectMapper objectMapper;
    private final DataInputStream input;
    private final DataOutputStream output;

    public JsonSocketChannel(InputStream inputStream, OutputStream outputStream) {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.input = new DataInputStream(inputStream);
        this.output = new DataOutputStream(outputStream);
    }

    public synchronized <T> void writeMessage(T message) throws IOException {
        String payload = objectMapper.writeValueAsString(message);
        long checksum = calculateChecksum(payload);
        NetworkEnvelope envelope = new NetworkEnvelope(payload, checksum);
        byte[] bytes = objectMapper.writeValueAsBytes(envelope);
        output.writeInt(bytes.length);
        output.write(bytes);
        output.flush();
    }

    public synchronized <T> T readMessage(Class<T> clazz) throws IOException {
        int size;
        try {
            size = input.readInt();
        } catch (EOFException eofException) {
            return null;
        }

        byte[] bytes = new byte[size];
        int read = input.read(bytes, 0, size);
        if (bytes.length < size) {
            throw new IOException("Incomplete network frame");
        }

        NetworkEnvelope envelope = objectMapper.readValue(bytes, NetworkEnvelope.class);
        long expected = calculateChecksum(envelope.getPayload());
        if (expected != envelope.getChecksum()) {
            throw new IOException("Checksum validation failed");
        }

        return objectMapper.readValue(envelope.getPayload(), clazz);
    }

    private long calculateChecksum(String value) {
        CRC32 crc32 = new CRC32();
        crc32.update(value.getBytes(StandardCharsets.UTF_8));
        return crc32.getValue();
    }
}
