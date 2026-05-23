package model.dao;

import config.DatabaseConnection;
import java.sql.*;

public class UserDAO {

    /**
     * Inserts a new user into the base users table, then creates their corresponding 
     * subclass profile in either buyers or sellers.
     */
    public boolean registerUser(String username, String email, String password, String role) {
        String insertUserSQL = "INSERT INTO users (username, email, password, base_role) VALUES (?, ?, ?, ?)";
        String insertBuyerSQL = "INSERT INTO buyers (buyer_id) VALUES (?)";
        String insertSellerSQL = "INSERT INTO sellers (seller_id, tier_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Start a transaction to ensure both inserts succeed together
            conn.setAutoCommit(false);

            // 1. Insert into base users table
            try (PreparedStatement stmt = conn.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, role);
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }

                // Retrieve the auto-generated user_id
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        // 2. Insert into the correct subclass profile table
                        if ("BUYER".equalsIgnoreCase(role)) {
                            try (PreparedStatement buyerStmt = conn.prepareStatement(insertBuyerSQL)) {
                                buyerStmt.setInt(1, userId);
                                buyerStmt.executeUpdate();
                            }
                        } else if ("SELLER".equalsIgnoreCase(role)) {
                            try (PreparedStatement sellerStmt = conn.prepareStatement(insertSellerSQL)) {
                                sellerStmt.setInt(1, userId);
                                sellerStmt.setInt(2, 1); // Defaults to Tier 1 (BRONZE)
                                sellerStmt.executeUpdate();
                            }
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            conn.commit(); // Save changes to the database safely
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Checks credentials against database and returns the user's role if valid.
     */
    public String authenticateUser(String username, String password) {
        String sql = "SELECT base_role FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // Note: In a production app, hash this!
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("base_role"); // Returns 'BUYER' or 'SELLER'
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Authentication failed
    }
}