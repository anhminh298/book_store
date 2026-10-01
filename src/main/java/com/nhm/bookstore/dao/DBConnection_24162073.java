package com.nhm.bookstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection_24162073 {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bookstore_db;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "Sa@123456";

    /** Transactional services use this method so connection failures propagate. */
    public static Connection openConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC driver is unavailable", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = openConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
