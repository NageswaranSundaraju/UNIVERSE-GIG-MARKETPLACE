package model.dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class NotificationDAO {

   
    public void pushNotification(int userId, String message) {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    
    
    public ArrayList<String> fetchUserNotifications(int userId) {
        ArrayList<String> messages = new ArrayList<>();
        String sql = "SELECT message FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 10";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(rs.getString("message"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return messages;
    }
}