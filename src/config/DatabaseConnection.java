package config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Singleton class to manage the MySQL Database Connection
 * for the UniVerse Gig Marketplace project.
 */
public class DatabaseConnection {
    
    // Database configuration credentials
    private static final String URL = "jdbc:mysql://localhost:3306/universe_marketplace";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Change this if your XAMPP/MySQL has a password

    // The single instance of the connection
    private static Connection connection = null;

    // Private constructor prevents instantiation from other classes
    private DatabaseConnection() {}

    /**
     * Provides global access to the single database connection instance.
     * Uses lazy initialization to connect only when requested.
     * 
     * @return Connection object to execute SQL queries
     */
    public static Connection getConnection() {
        try {
            // Check if connection doesn't exist, or was closed unexpectedly
            if (connection == null || connection.isClosed()) {
                // Explicitly load the MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish the live connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[SUCCESS] Database connected successfully to universe_marketplace.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] MySQL JDBC Driver not found! Ensure the connector JAR is added to libraries.");
            JOptionPane.showMessageDialog(null, 
                    "Database Driver Missing!\nPlease add the MySQL Connector/J JAR to your project libraries.", 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to connect to MySQL database.");
            JOptionPane.showMessageDialog(null, 
                    "Database Connection Failed!\nPlease ensure XAMPP (MySQL) is running.", 
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Safely closes the database connection when the application shuts down.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[INFO] Database connection closed safely.");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to close database connection cleanly.");
            e.printStackTrace();
        }
    }
}