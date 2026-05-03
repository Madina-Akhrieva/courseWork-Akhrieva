package org.example.coursework2026marina.server.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class SchemaInitializer {
    private final DatabaseManager databaseManager;

    public SchemaInitializer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void initialize() {
        try (Connection connection = databaseManager.getConnection();
             Statement statement = connection.createStatement()) {
            executeSqlScript(statement, "db/schema.sql", "Schema");
            executeSqlScript(statement, "db/demo-data.sql", "Demo data");
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot initialize schema", exception);
        }
    }

    private void executeSqlScript(Statement statement, String resourcePath, String scriptName) {
        String sql = readSql(resourcePath);
        if (sql == null || sql.trim().isEmpty()) {
            return;
        }

        Arrays.stream(sql.split(";"))
                .map(String::trim)
                .filter(part -> part.length() > 0)
                .forEach(part -> {
                    try {
                        statement.execute(part);
                    } catch (SQLException exception) {
                        throw new IllegalStateException(scriptName + " execution failed: " + exception.getMessage(), exception);
                    }
                });
    }

    private String readSql(String resourcePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            return builder.toString();
        } catch (IOException exception) {
            return "";
        }
    }
}
