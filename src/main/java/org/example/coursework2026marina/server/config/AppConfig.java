package org.example.coursework2026marina.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public final class AppConfig {
    private static volatile AppConfig instance;

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String serverHost;
    private final int serverPort;
    private final int workerThreads;

    private AppConfig(String dbUrl,
                      String dbUser,
                      String dbPassword,
                      String serverHost,
                      int serverPort,
                      int workerThreads) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.workerThreads = workerThreads;
    }

    public static AppConfig getInstance(String[] args) {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = load(args);
                }
            }
        }
        return instance;
    }

    private static AppConfig load(String[] args) {
        Properties fileProperties = new Properties();
        try (InputStream stream = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (stream != null) {
                fileProperties.load(stream);
            }
        } catch (IOException ignored) {
        }

        String dbUrl = pickValue("APP_DB_URL", argValue(args, "--db-url"), fileProperties.getProperty("app.db.url"),
                "jdbc:postgresql://localhost:5432/edu_analytics");
        String dbUser = pickValue("APP_DB_USER", argValue(args, "--db-user"), fileProperties.getProperty("app.db.user"),
                "edu_admin");
        String dbPassword = pickValue("APP_DB_PASSWORD", argValue(args, "--db-password"), fileProperties.getProperty("app.db.password"),
                "edu_admin");
        String host = pickValue("APP_SERVER_HOST", argValue(args, "--host"), fileProperties.getProperty("app.server.host"),
                "0.0.0.0");
        int port = Integer.parseInt(pickValue("APP_SERVER_PORT", argValue(args, "--port"), fileProperties.getProperty("app.server.port"),
                "5051"));
        int threads = Integer.parseInt(pickValue("APP_SERVER_THREADS", argValue(args, "--threads"), fileProperties.getProperty("app.server.threads"),
                "16"));

        return new AppConfig(dbUrl, dbUser, dbPassword, host, port, threads);
    }

    private static String argValue(String[] args, String key) {
        return Arrays.stream(args)
                .filter(Objects::nonNull)
                .filter(arg -> arg.startsWith(key + "="))
                .map(arg -> arg.substring((key + "=").length()))
                .findFirst()
                .orElse(null);
    }

    private static String pickValue(String envKey, String argValue, String fileValue, String defaultValue) {
        String env = System.getenv(envKey);
        if (env != null && !env.isEmpty() && env.trim().length() > 0) {
            return env;
        }
        if (argValue != null && !argValue.isEmpty() && argValue.trim().length() > 0) {
            return argValue;
        }
        if (fileValue != null && !fileValue.isEmpty() && fileValue.trim().length() > 0) {
            return fileValue;
        }
        return defaultValue;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getServerHost() {
        return serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }
}
