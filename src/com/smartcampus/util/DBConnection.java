package com.smartcampus.util;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton database connection and lifecycle manager.
 * Connects to SQLite database and ensures schema and seeds are loaded.
 */
public class DBConnection {
    private static final String DB_FILE = "smart_campus.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE;

    static {
        try {
            // Load SQLite JDBC Driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] SQLite JDBC Driver not found in classpath.");
        }
    }

    private DBConnection() {}

    /**
     * Obtains an active connection to the SQLite database.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(JDBC_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Initializes the database if the schema has not yet been applied.
     */
    public static void initializeDatabaseIfMissing() {
        File dbFile = new File(DB_FILE);
        if (!dbFile.exists() || dbFile.length() == 0) {
            System.out.println("[INFO] Initializing new SQLite database from schema.sql...");
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                
                if (Files.exists(Paths.get("database/schema.sql"))) {
                    String schemaSql = new String(Files.readAllBytes(Paths.get("database/schema.sql")));
                    for (String query : schemaSql.split(";")) {
                        if (!query.trim().isEmpty()) {
                            stmt.execute(query.trim());
                        }
                    }
                }
                
                if (Files.exists(Paths.get("database/sample_data.sql"))) {
                    String dataSql = new String(Files.readAllBytes(Paths.get("database/sample_data.sql")));
                    for (String query : dataSql.split(";")) {
                        if (!query.trim().isEmpty()) {
                            stmt.execute(query.trim());
                        }
                    }
                }
                System.out.println("[INFO] Database initialization complete.");
            } catch (Exception e) {
                System.err.println("[WARN] Database auto-initialization check: " + e.getMessage());
            }
        }
    }
}
