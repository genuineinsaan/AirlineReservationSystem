package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // UPDATED: Connects to the new V2 database
    private static final String URL = "jdbc:mysql://localhost:3306/airline_system_v2";
    private static final String USER = "root";    // your MySQL username
    private static final String PASS = "sanju26"; // your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
    // Quick test to make sure connection works
    public static void main(String[] args) {
        try {
            getConnection();
            System.out.println("✅ Connection to airline_system_v2 successful!");
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}