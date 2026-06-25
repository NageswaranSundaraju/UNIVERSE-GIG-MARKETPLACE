package model.dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Vector;


public class BookingDAO {

   
    public boolean createBooking(int buyerId, int gigId, int quantity, boolean isExpress, double totalPrice) {
        String sql = "INSERT INTO bookings (buyer_id, gig_id, quantity, is_express, total_price, order_status) VALUES (?, ?, ?, ?, ?, 'PENDING')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, buyerId);
            stmt.setInt(2, gigId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, isExpress ? 1 : 0);
            stmt.setDouble(5, totalPrice);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
  
    public java.util.ArrayList<java.util.Vector<Object>> getBuyerBookingLogs(int buyerId) {
        java.util.ArrayList<java.util.Vector<Object>> logList = new java.util.ArrayList<>();
        
        
        String sql = "SELECT b.booking_id, g.title, u.username AS seller_name, b.total_price, b.order_status " +
                     "FROM bookings b " +
                     "JOIN gigs g ON b.gig_id = g.gig_id " +
                     "JOIN users u ON g.seller_id = u.user_id " +
                     "WHERE b.buyer_id = ? " +
                     "ORDER BY b.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, buyerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Vector<Object> row = new java.util.Vector<>();
                    row.add(rs.getInt("booking_id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("seller_name"));
                    row.add(rs.getBigDecimal("total_price"));
                    row.add(rs.getString("order_status"));
                    logList.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logList;
    }
    
    
    public java.util.ArrayList<java.util.Vector<Object>> getSellerIncomingRequests(int sellerId) {
        java.util.ArrayList<java.util.Vector<Object>> requestList = new java.util.ArrayList<>();
        
        String sql = "SELECT b.booking_id, g.title, u.username AS buyer_name, b.quantity, b.total_price, b.order_status " +
                     "FROM bookings b " +
                     "JOIN gigs g ON b.gig_id = g.gig_id " +
                     "JOIN users u ON b.buyer_id = u.user_id " +
                     "WHERE g.seller_id = ? AND b.order_status = 'PENDING' " +
                     "ORDER BY b.created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sellerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Vector<Object> row = new java.util.Vector<>();
                    row.add(rs.getInt("booking_id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("buyer_name"));
                    row.add(rs.getInt("quantity"));
                    row.add(rs.getBigDecimal("total_price"));
                    row.add(rs.getString("order_status"));
                    requestList.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requestList;
    }

   
    public boolean updateBookingStatus(int bookingId, String newStatus) {
        String sql = "UPDATE bookings SET order_status = ? WHERE booking_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public java.util.ArrayList<java.util.Vector<Object>> getSellerActiveJobs(int sellerId) {
        java.util.ArrayList<java.util.Vector<Object>> jobList = new java.util.ArrayList<>();
        
        String sql = "SELECT b.booking_id, g.title, u.username AS buyer_name, b.quantity, b.total_price, b.order_status " +
                     "FROM bookings b " +
                     "JOIN gigs g ON b.gig_id = g.gig_id " +
                     "JOIN users u ON b.buyer_id = u.user_id " +
                     "WHERE g.seller_id = ? AND b.order_status = 'IN_PROGRESS' " +
                     "ORDER BY b.created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sellerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Vector<Object> row = new java.util.Vector<>();
                    row.add(rs.getInt("booking_id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("buyer_name"));
                    row.add(rs.getInt("quantity"));
                    row.add(rs.getBigDecimal("total_price"));
                    row.add(rs.getString("order_status"));
                    jobList.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jobList;
    }
    
   
    public boolean completeJobAndAwardPoints(int bookingId, int buyerId, int sellerId, double projectValue, int pointsEarned) {
        

        String checkWalletSql = "SELECT wallet_balance FROM buyers WHERE buyer_id = ?";
        

        String deductBuyerSql = "UPDATE buyers SET wallet_balance = wallet_balance - ? WHERE buyer_id = ?";
        
        
        String updateBookingSql = "UPDATE bookings SET order_status = 'COMPLETED' WHERE booking_id = ?";
        
        
        String updateSellerSql = "UPDATE sellers SET service_points = service_points + ?, total_earnings = total_earnings + ? WHERE seller_id = ?";
        
       
        String checkPointsSql = "SELECT service_points FROM sellers WHERE seller_id = ?";
        String updateTierSql = "UPDATE sellers SET tier_id = ? WHERE seller_id = ?";

        Connection conn = null;
        try {
            conn = config.DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            
            double currentWalletBalance = 0.0;
            try (PreparedStatement stmtCheck = conn.prepareStatement(checkWalletSql)) {
                stmtCheck.setInt(1, buyerId);
                try (ResultSet rs = stmtCheck.executeQuery()) {
                    if (rs.next()) {
                        currentWalletBalance = rs.getDouble("wallet_balance");
                    }
                }
            }

            
            if (currentWalletBalance < projectValue) {
                conn.rollback(); 
                return false; 
            }

        
            try (PreparedStatement stmtDeduct = conn.prepareStatement(deductBuyerSql)) {
                stmtDeduct.setDouble(1, projectValue);
                stmtDeduct.setInt(2, buyerId);
                stmtDeduct.executeUpdate();
            }

           
            try (PreparedStatement stmtBooking = conn.prepareStatement(updateBookingSql)) {
                stmtBooking.setInt(1, bookingId);
                stmtBooking.executeUpdate();
            }

            
            try (PreparedStatement stmtSeller = conn.prepareStatement(updateSellerSql)) {
                stmtSeller.setInt(1, pointsEarned);
                stmtSeller.setDouble(2, projectValue);
                stmtSeller.setInt(3, sellerId);
                stmtSeller.executeUpdate();
            }

            
            int updatedPointsCount = 0;
            try (PreparedStatement stmtPoints = conn.prepareStatement(checkPointsSql)) {
                stmtPoints.setInt(1, sellerId);
                try (ResultSet rsPoints = stmtPoints.executeQuery()) {
                    if (rsPoints.next()) {
                        updatedPointsCount = rsPoints.getInt("service_points");
                    }
                }
            }

            
            int newTierMappingId = 1; 
            if (updatedPointsCount >= 150) {
                newTierMappingId = 3;
            } else if (updatedPointsCount >= 50) {
                newTierMappingId = 2; 
            }

            try (PreparedStatement stmtTier = conn.prepareStatement(updateTierSql)) {
                stmtTier.setInt(1, newTierMappingId);
                stmtTier.setInt(2, sellerId);
                stmtTier.executeUpdate();
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
    
    public HashMap<String, Object> getSellerAnalytics(int sellerId) {
        HashMap<String, Object> metrics = new HashMap<>();
        
        
        metrics.put("total_earnings", 0.0);
        metrics.put("completed_jobs", 0);
        metrics.put("pending_requests", 0);
        metrics.put("success_rate", 100);

        String earningsSql = "SELECT SUM(total_price) AS total FROM bookings b " +
                             "JOIN gigs g ON b.gig_id = g.gig_id " +
                             "WHERE g.seller_id = ? AND b.order_status = 'COMPLETED'";
                             
        String jobCountsSql = "SELECT " +
                              "COUNT(CASE WHEN b.order_status = 'COMPLETED' THEN 1 END) as completed, " +
                              "COUNT(CASE WHEN b.order_status = 'PENDING' THEN 1 END) as pending, " +
                              "COUNT(*) as total_orders " +
                              "FROM bookings b " +
                              "JOIN gigs g ON b.gig_id = g.gig_id " +
                              "WHERE g.seller_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            
            try (PreparedStatement stmt = conn.prepareStatement(earningsSql)) {
                stmt.setInt(1, sellerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) metrics.put("total_earnings", rs.getDouble("total"));
                }
            }
            
            
            try (PreparedStatement stmt = conn.prepareStatement(jobCountsSql)) {
                stmt.setInt(1, sellerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int completed = rs.getInt("completed");
                        int pending = rs.getInt("pending");
                        int total = rs.getInt("total_orders");
                        
                        metrics.put("completed_jobs", completed);
                        metrics.put("pending_requests", pending);
                        
                        if (total > 0) {
                            int successRate = (int) (((double) completed / total) * 180); 
                            metrics.put("success_rate", Math.min(successRate, 100)); 
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return metrics;
    }

    
    public int getSellerIdFromBooking(int bookingId) {
        String sql = "SELECT g.seller_id FROM bookings b JOIN gigs g ON b.gig_id = g.gig_id WHERE b.booking_id = ?";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { return rs.getInt("seller_id"); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }
    
    
    public ArrayList<Vector<Object>> getActiveBookings(int buyerId) {
        ArrayList<Vector<Object>> list = new ArrayList<>();
        String sql = "SELECT b.booking_id, g.title, u.username, b.total_price, b.order_status " +
                     "FROM bookings b " +
                     "JOIN gigs g ON b.gig_id = g.gig_id " +
                     "JOIN users u ON g.seller_id = u.user_id " +
                     "WHERE b.buyer_id = ? AND b.order_status IN ('PENDING', 'IN_PROGRESS', 'READY_FOR_REVIEW') " +
                     "ORDER BY b.booking_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("booking_id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("username"));
                    row.add(rs.getString("total_price"));
                    row.add(rs.getString("order_status"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

   
    public ArrayList<Vector<Object>> getRecentHistoryBookings(int buyerId) {
        ArrayList<Vector<Object>> list = new ArrayList<>();
        String sql = "SELECT b.booking_id, g.title, u.username, b.total_price, b.order_status " +
                     "FROM bookings b " +
                     "JOIN gigs g ON b.gig_id = g.gig_id " +
                     "JOIN users u ON g.seller_id = u.user_id " +
                     "WHERE b.buyer_id = ? AND b.order_status IN ('COMPLETED', 'CANCELLED', 'READY_FOR_REVIEW') " +
                     "ORDER BY b.booking_id DESC LIMIT 5"; // Enforces the max 5-row constraint cleanly
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("booking_id"));
                    row.add(rs.getString("title"));
                    row.add(rs.getString("username"));
                    row.add(rs.getString("total_price"));
                    row.add(rs.getString("order_status"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
    public java.util.HashMap<String, Object> getBookingDetailedManifest(int orderId) {
        java.util.HashMap<String, Object> details = new java.util.HashMap<>();
        
      
        String sql = "SELECT b.booking_id, b.order_status, b.total_price, b.quantity, " +
                     "g.title, g.category, g.description AS gig_desc, " +
                     "u.username AS seller_name, u.email AS seller_email, " +
                     "t.tier_name " +
                     "FROM bookings b " +
                     "JOIN gigs g ON b.gig_id = g.gig_id " +
                     "JOIN users u ON g.seller_id = u.user_id " +
                     "LEFT JOIN sellers s ON u.user_id = s.seller_id " + 
                     "LEFT JOIN tiers t ON s.tier_id = t.tier_id " +     
                     "WHERE b.booking_id = ?";
                     
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    
                    details.put("booking_id", rs.getInt("booking_id"));
                    details.put("order_id", rs.getInt("booking_id")); 
                    
                    details.put("status", rs.getString("order_status"));
                    details.put("total_price", rs.getDouble("total_price"));
                    details.put("quantity", rs.getInt("quantity"));
                    details.put("title", rs.getString("title"));
                    details.put("category", rs.getString("category"));
                    details.put("gig_desc", rs.getString("gig_desc"));
                    details.put("seller_name", rs.getString("seller_name"));
                    details.put("seller_email", rs.getString("seller_email"));
                    
                    
                    String tier = rs.getString("tier_name");
                    details.put("tier_name", (tier != null) ? tier : "Bronze");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    public double getBuyerTotalExpenses(int buyerId) {
        String sql = "SELECT SUM(total_price) AS total_spend FROM bookings WHERE buyer_id = ? AND order_status = 'COMPLETED'";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_spend");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    
    public double getBuyerWalletBalance(int buyerId) {
        String sql = "SELECT wallet_balance FROM buyers WHERE buyer_id = ?";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("wallet_balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public String getSellerEmailFromBooking(int orderId) {
        String sql = "SELECT u.email FROM bookings b JOIN gigs g ON b.gig_id = g.gig_id JOIN users u ON g.seller_id = u.user_id WHERE b.booking_id = ?";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return rs.getString("email"); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public String getBuyerEmailFromBooking(int orderId) {
        String sql = "SELECT u.email FROM bookings b JOIN users u ON b.buyer_id = u.user_id WHERE b.booking_id = ?";
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return rs.getString("email"); }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    
    
    public java.util.HashMap<String, String> getOrderNotificationMetadataBySql(int gigId, int buyerId) {
        java.util.HashMap<String, String> result = new java.util.HashMap<>();
        String sql = "SELECT " +
                     "  s.email AS seller_email, " +
                     "  g.title AS task_title, " +
                     "  b.username AS buyer_name, " +
                     "  b.email AS buyer_email " +
                     "FROM gigs g " +
                     "JOIN users s ON g.seller_id = s.user_id " + 
                     "JOIN users b ON b.user_id = ? " +           
                     "WHERE g.gig_id = ?";
                     
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);
            stmt.setInt(2, gigId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result.put("seller_email", rs.getString("seller_email"));
                    result.put("task_title", rs.getString("task_title"));
                    result.put("buyer_name", rs.getString("buyer_name"));
                    result.put("buyer_email", rs.getString("buyer_email"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public java.util.HashMap<String, String> getBookingDetailsForEmailBySql(int bookingId) {
        java.util.HashMap<String, String> result = new java.util.HashMap<>();
        String sql = "SELECT " +
                     "  g.title AS task_title, " +
                     "  b.username AS buyer_name, " +
                     "  b.email AS buyer_email, " +
                     "  s.username AS seller_name, " +
                     "  bo.quantity AS qty, " +
                     "  bo.is_express AS express_flag, " +
                     "  bo.total_price AS total_cost " +
                     "FROM bookings bo " +
                     "JOIN gigs g ON bo.gig_id = g.gig_id " +
                     "JOIN users s ON g.seller_id = s.user_id " +
                     "JOIN users b ON bo.buyer_id = b.user_id " +
                     "WHERE bo.booking_id = ?"; 
                     
        try (Connection conn = config.DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result.put("task_title", rs.getString("task_title"));
                    result.put("buyer_name", rs.getString("buyer_name"));
                    result.put("buyer_email", rs.getString("buyer_email"));
                    result.put("seller_name", rs.getString("seller_name"));
                    result.put("qty", String.valueOf(rs.getInt("qty")));
                    result.put("express_flag", rs.getBoolean("express_flag") ? "Express (Priority Surcharge)" : "Standard Execution");
                    result.put("total_cost", String.format("%.2f", rs.getDouble("total_cost")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
   
    
}