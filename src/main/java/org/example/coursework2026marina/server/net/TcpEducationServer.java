package org.example.coursework2026marina.server.net;

import org.example.coursework2026marina.server.config.AppConfig;
import org.example.coursework2026marina.server.db.DatabaseManager;
import org.example.coursework2026marina.server.repository.RepositoryFactory;
import org.example.coursework2026marina.server.security.PasswordEncoderFactory;
import org.example.coursework2026marina.server.service.AdminService;
import org.example.coursework2026marina.server.service.AnalyticsService;
import org.example.coursework2026marina.server.service.AuthService;
import org.example.coursework2026marina.server.service.RequestRouter;
import org.example.coursework2026marina.server.service.StudentService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpEducationServer {
    private final AppConfig config;
    private final ExecutorService workers;
    private final RequestRouter router;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public TcpEducationServer(AppConfig config, DatabaseManager databaseManager) {
        this.config = config;
        this.workers = Executors.newFixedThreadPool(config.getWorkerThreads());
        
        RepositoryFactory factory = new RepositoryFactory(databaseManager);
        AuthService authService = new AuthService(factory.userRepository(), PasswordEncoderFactory.createDefault());
        AdminService adminService = new AdminService(
                factory.programRepository(),
                factory.moduleRepository(),
                factory.gradeRepository(),
                factory.assignmentRepository(),
                factory.submissionRepository(),
                factory.userRepository()
        );
        StudentService studentService = new StudentService(
                factory.programRepository(),
                factory.gradeRepository(),
                factory.enrollmentRepository(),
                factory.assignmentRepository(),
                factory.submissionRepository()
        );
        AnalyticsService analyticsService = new AnalyticsService(factory.analyticsRepository());
        
        this.router = new RequestRouter(authService, adminService, studentService, analyticsService);
    }

    public void start() {
        running = true;
        try (ServerSocket socket = new ServerSocket(config.getServerPort())) {
            this.serverSocket = socket;
            System.out.println("Education server started on " + config.getServerHost() + ":" + config.getServerPort());
            while (running) {
                Socket client = socket.accept();
                workers.submit(new ClientSessionHandler(client, router));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Server failed on port " + config.getServerPort(), exception);
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }
        workers.shutdownNow();
    }
}
