package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/universe_marketplace";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	private static Connection connection = null;

	private DatabaseConnection() {
	}

	public static Connection getConnection() {
		try {

			if (connection == null || connection.isClosed()) {

				Class.forName("com.mysql.cj.jdbc.Driver");

				connection = DriverManager.getConnection(URL, USER, PASSWORD);

			}
		} catch (ClassNotFoundException e) {
			System.err.println("[ERROR] MySQL JDBC Driver not found! Ensure the connector JAR is added to libraries.");
			JOptionPane.showMessageDialog(null,
					"Database Driver Missing!\nPlease add the MySQL Connector/J JAR to your project libraries.",
					"Database Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("[ERROR] Failed to connect to MySQL database.");
			JOptionPane.showMessageDialog(null, "Database Connection Failed!\nPlease ensure XAMPP (MySQL) is running.",
					"Connection Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return connection;
	}

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