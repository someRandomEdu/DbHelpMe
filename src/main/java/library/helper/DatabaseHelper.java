package library.helper;

import java.sql.*;

public class DatabaseHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "130405";
    private static Connection connection;

    public static void connectToDatabase() {
        try {

            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connect to database successfully");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
}
