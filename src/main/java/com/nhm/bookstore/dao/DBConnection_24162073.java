package com.nhm.bookstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection_24162073 {
    private static final String DEFAULT_URL =
            "jdbc:sqlserver://localhost:1433;databaseName=bookstore_db;encrypt=true;trustServerCertificate=true";

    public static Connection openConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC driver is unavailable", e);
        }

        String url = setting("bookstore.jdbc.url", "BOOKSTORE_JDBC_URL", DEFAULT_URL);
        String user = setting("bookstore.jdbc.user", "BOOKSTORE_JDBC_USER", null);
        String password = setting("bookstore.jdbc.password", "BOOKSTORE_JDBC_PASSWORD", null);
        if (user == null || password == null) {
            throw new SQLException("Set BOOKSTORE_JDBC_USER and BOOKSTORE_JDBC_PASSWORD before starting the app.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    /** Convenience alias for non-transactional DAO calls; never returns null. */
    public static Connection getConnection() throws SQLException {
        return openConnection();
    }

    private static String setting(String property, String environmentVariable, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        if (value == null || value.isBlank()) return defaultValue;
        return value;
    }
}
