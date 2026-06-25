package model.dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

public class GigDAO {

  
    public ArrayList<Vector<Object>> searchGigs(String keyword, String category) {
        ArrayList<Vector<Object>> gigList = new ArrayList<>();
        
        
        StringBuilder sql = new StringBuilder("SELECT gig_id, title, category, base_price, seller_id FROM gigs WHERE is_deleted = 0");
        
        if (!keyword.isEmpty()) {
            sql.append(" AND title LIKE ?");
        }
        if (!"All Categories".equalsIgnoreCase(category)) {
            sql.append(" AND category = ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (!keyword.isEmpty()) {
                stmt.setString(paramIndex++, "%" + keyword + "%");
            }
            if (!"All Categories".equalsIgnoreCase(category)) {
                stmt.setString(paramIndex, category);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("gig_id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("category"));
                    row.add(rs.getBigDecimal("base_price"));
                    row.add(rs.getInt("seller_id"));
                    gigList.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gigList;
    }
    
    
    public boolean createGig(int sellerId, String title, String category, double price, String description) {
        String sql = "INSERT INTO gigs (seller_id, title, category, base_price, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sellerId);
            stmt.setString(2, title);
            stmt.setString(3, category);
            stmt.setDouble(4, price);
            stmt.setString(5, description);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public boolean softDeleteGig(int gigId) {
        String sql = "UPDATE gigs SET is_deleted = 1 WHERE gig_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, gigId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public ArrayList<java.util.Vector<Object>> getGigsBySeller(int sellerId) {
        ArrayList<java.util.Vector<Object>> list = new ArrayList<>();
        String sql = "SELECT gig_id, title, category, base_price FROM gigs WHERE seller_id = ? AND is_deleted = 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sellerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Vector<Object> row = new java.util.Vector<>();
                    row.add(rs.getInt("gig_id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("category"));
                    row.add(rs.getBigDecimal("base_price"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
    public java.util.HashMap<String, Object> getGigDetailedManifest(int gigId) {
        java.util.HashMap<String, Object> details = new java.util.HashMap<>();
        
       
        String sql = "SELECT g.gig_id, g.title, g.category, g.base_price, g.description, " +
                     "u.username AS seller_name, u.email AS seller_email, " +
                     "t.tier_name " +
                     "FROM gigs g " +
                     "JOIN users u ON g.seller_id = u.user_id " +
                     "LEFT JOIN sellers s ON u.user_id = s.seller_id " + 
                     "LEFT JOIN tiers t ON s.tier_id = t.tier_id " +     
                     "WHERE g.gig_id = ?";
                     
        try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gigId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    details.put("gig_id", rs.getInt("gig_id"));
                    details.put("title", rs.getString("title"));
                    details.put("category", rs.getString("category"));
                    details.put("base_price", rs.getDouble("base_price"));
                    details.put("description", rs.getString("description"));
                    details.put("seller_name", rs.getString("seller_name"));
                    details.put("seller_email", rs.getString("seller_email"));
                    
                    
                    String tier = rs.getString("tier_name");
                    details.put("tier_name", (tier != null) ? tier : "Bronze");
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    
 
    public java.util.ArrayList<java.util.Vector<Object>> getAllGigsForAdmin() {
        java.util.ArrayList<java.util.Vector<Object>> list = new java.util.ArrayList<>();
        String sql = "SELECT g.gig_id, g.title, g.category, u.username FROM gigs g JOIN users u ON g.seller_id = u.user_id";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                java.util.Vector<Object> row = new java.util.Vector<>();
                row.add(rs.getInt("gig_id"));
                row.add(rs.getString("title"));
                row.add(rs.getString("category"));
                row.add(rs.getString("username"));
                list.add(row);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    
    public boolean deleteGigAdmin(int gigId) {
        String sql = "UPDATE gigs SEt is_deleted = 1 WHERE gig_id = ?";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gigId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}