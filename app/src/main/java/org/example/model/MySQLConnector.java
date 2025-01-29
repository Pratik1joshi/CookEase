package org.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnector {
    static final String dataBaseURL = "jdbc:mysql://localhost:3306/CookEase";
    static final String username = "root";
    static final String password = "pass123";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(dataBaseURL, username, password);
        if (conn == null) {
            throw new SQLException("Failed to establish a database connection.");
        }
        return conn;
    }
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Database connected: " + (conn != null));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
