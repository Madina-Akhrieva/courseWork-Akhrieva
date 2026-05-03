package org.example.coursework2026marina.server.repository;

import org.example.coursework2026marina.server.db.DatabaseManager;

public class AbstractJdbcRepository {
    protected final DatabaseManager databaseManager;

    protected AbstractJdbcRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }
}
