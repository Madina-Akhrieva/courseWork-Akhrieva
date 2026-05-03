package org.example.coursework2026marina.server;

import org.example.coursework2026marina.server.config.AppConfig;
import org.example.coursework2026marina.server.db.DatabaseManager;
import org.example.coursework2026marina.server.db.SchemaInitializer;
import org.example.coursework2026marina.server.net.TcpEducationServer;

public class ServerLauncher {
    public static void main(String[] args) {
        AppConfig config = AppConfig.getInstance(args);
        DatabaseManager databaseManager = DatabaseManager.getInstance(config);
        new SchemaInitializer(databaseManager).initialize();

        TcpEducationServer server = new TcpEducationServer(config, databaseManager);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}
