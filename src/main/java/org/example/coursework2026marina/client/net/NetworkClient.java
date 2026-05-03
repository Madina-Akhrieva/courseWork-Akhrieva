package org.example.coursework2026marina.client.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.coursework2026marina.common.JsonSocketChannel;
import org.example.coursework2026marina.common.Request;
import org.example.coursework2026marina.common.Response;

import java.io.IOException;
import java.net.Socket;

public class NetworkClient {
    private Socket socket;
    private JsonSocketChannel channel;
    private String serverHost;
    private int serverPort;

    public NetworkClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public boolean connect() {
        try {
            socket = new Socket(serverHost, serverPort);
            channel = new JsonSocketChannel(socket.getInputStream(), socket.getOutputStream());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Response sendRequest(Request request) throws IOException {
        if (channel == null || socket.isClosed()) {
            throw new IOException("Not connected to server");
        }
        channel.writeMessage(request);
        return channel.readMessage(Response.class);
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
