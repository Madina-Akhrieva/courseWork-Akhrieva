package org.example.coursework2026marina.server.net;

import org.example.coursework2026marina.common.JsonSocketChannel;
import org.example.coursework2026marina.common.Request;
import org.example.coursework2026marina.common.Response;
import org.example.coursework2026marina.server.service.RequestRouter;

import java.io.IOException;
import java.net.Socket;

public class ClientSessionHandler implements Runnable {
    private final Socket socket;
    private final RequestRouter router;

    public ClientSessionHandler(Socket socket, RequestRouter router) {
        this.socket = socket;
        this.router = router;
    }

    @Override
    public void run() {
        JsonSocketChannel channel = null;
        try {
            channel = new JsonSocketChannel(socket.getInputStream(), socket.getOutputStream());
            while (!socket.isClosed()) {
                Request request = channel.readMessage(Request.class);
                if (request == null) {
                    break;
                }
                Response response = router.route(request);
                channel.writeMessage(response);
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
