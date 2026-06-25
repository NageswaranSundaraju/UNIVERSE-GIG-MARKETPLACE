package model.dao;

import config.DatabaseConnection;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class UserDAO {

   
    public boolean registerUser(String username, String email, String password, String role) {
        String insertUserSQL = "INSERT INTO users (username, email, password, base_role) VALUES (?, ?, ?, ?)";
        String insertBuyerSQL = "INSERT INTO buyers (buyer_id) VALUES (?)";
        String insertSellerSQL = "INSERT INTO sellers (seller_id, tier_id) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            conn.setAutoCommit(false);

            
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

                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        if ("BUYER".equalsIgnoreCase(role)) {
                            try (PreparedStatement buyerStmt = conn.prepareStatement(insertBuyerSQL)) {
                                buyerStmt.setInt(1, userId);
                                buyerStmt.executeUpdate();
                            }
                        } else if ("SELLER".equalsIgnoreCase(role)) {
                            try (PreparedStatement sellerStmt = conn.prepareStatement(insertSellerSQL)) {
                                sellerStmt.setInt(1, userId);
                                sellerStmt.setInt(2, 1); 
                                sellerStmt.executeUpdate();
                            }
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            conn.commit(); 
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

    
    public int authenticateUser(String username, String password) {
        // ➕ Added is_active to the SQL SELECT statement
        String sql = "SELECT user_id, is_active FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                   
                    if (!rs.getBoolean("is_active")) {
                        return -2; 
                    }
                    return rs.getInt("user_id"); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }

    
    public String getUserRoleById(int userId) {
        String sql = "SELECT base_role FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("base_role");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    
    public Map<String, Object> getBuyerProfile(int buyerId) {
        Map<String, Object> profile = new HashMap<>();
        String sql = "SELECT u.username, u.email, b.wallet_balance, b.delivery_address " +
                     "FROM users u JOIN buyers b ON u.user_id = b.buyer_id WHERE u.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile.put("username", rs.getString("username"));
                    profile.put("email", rs.getString("email"));
                    profile.put("wallet_balance", rs.getDouble("wallet_balance"));
                    profile.put("delivery_address", rs.getString("delivery_address"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return profile;
    }

   
    public Map<String, Object> getSellerProfile(int sellerId) {
        Map<String, Object> profile = new HashMap<>();
        String sql = "SELECT u.username, u.email, s.service_points, s.total_earnings, t.tier_name " +
                     "FROM users u " +
                     "JOIN sellers s ON u.user_id = s.seller_id " +
                     "JOIN tiers t ON s.tier_id = t.tier_id WHERE u.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile.put("username", rs.getString("username"));
                    profile.put("email", rs.getString("email"));
                    profile.put("service_points", rs.getInt("service_points"));
                    profile.put("total_earnings", rs.getDouble("total_earnings"));
                    profile.put("tier_name", rs.getString("tier_name"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return profile;
    }

    
    public boolean topUpBuyerWallet(int buyerId, double amount) {
        String sql = "UPDATE buyers SET wallet_balance = wallet_balance + ? WHERE buyer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, buyerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean deactivateUserAccount(int userId) {
        String sql = "UPDATE users SET is_active = FALSE WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    

    public java.util.ArrayList<java.util.Vector<Object>> getAllUsersForAdmin() {
        java.util.ArrayList<java.util.Vector<Object>> list = new java.util.ArrayList<>();
        String sql = "SELECT user_id, username, email, base_role, is_active FROM users";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                java.util.Vector<Object> row = new java.util.Vector<>();
                row.add(rs.getInt("user_id"));
                row.add(rs.getString("username"));
                row.add(rs.getString("email"));
                row.add(rs.getString("base_role"));
                row.add(rs.getBoolean("is_active") ? "ACTIVE" : "SUSPENDED");
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }


    public boolean toggleUserStatus(int userId, boolean makeActive) {
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, makeActive);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}

    
