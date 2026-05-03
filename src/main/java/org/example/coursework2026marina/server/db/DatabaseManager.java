package org.example.coursework2026marina.server.db;

import org.example.coursework2026marina.server.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {
    private static volatile DatabaseManager instance;
    private final AppConfig config;

    private DatabaseManager(AppConfig config) {
        this.config = config;
    }

    public static DatabaseManager getInstance(AppConfig config) {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager(config);
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(config.getDbUrl(), config.getDbUser(), config.getDbPassword());
    }
}
