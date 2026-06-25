package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import model.dao.BookingDAO;

public class SellerAnalyticsPanel extends JPanel {
    private int sellerId;
    private BookingDAO bookingDAO;

    // Component metrics labels
    private JLabel lblEarningsVal;
    private JLabel lblCompletedVal;
    private JLabel lblPendingVal;
    private JLabel lblSuccessRateVal;

    // Design System UI Theme Colors
    private final Color COLOR_BG         = new Color(24, 28, 36);   
    private final Color COLOR_CARD       = new Color(17, 19, 24);   
    private final Color COLOR_ACCENT     = new Color(81, 107, 240);  
    private final Color COLOR_TEXT_LIGHT = new Color(245, 246, 250); 
    private final Color COLOR_TEXT_MUTED = new Color(140, 147, 171); 
    private final Color COLOR_BORDER     = new Color(45, 52, 71);    
    private final Color COLOR_SUCCESS    = new Color(46, 204, 113);

    public SellerAnalyticsPanel(int sellerId) {
        this.sellerId = sellerId;
        this.bookingDAO = new BookingDAO();

        setBackground(COLOR_BG);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- SECTION HEADER BAR ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Performance Matrix & Revenue Stream Engine");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_LIGHT);
        
        JButton btnRefresh = new JButton("Sync Live Metrics");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRefresh.setForeground(COLOR_TEXT_LIGHT);
        btnRefresh.setBackground(new Color(45, 52, 71));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshLiveMetrics());
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // --- ANALYTICS INSIGHTS GRID MATRIX ---
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setOpaque(false);

        // Instantiate Metric Cards
        JPanel cardEarnings = createMetricCard("TOTAL GENERATED REVENUE", lblEarningsVal = new JLabel("RM 0.00"), COLOR_SUCCESS);
        JPanel cardCompleted = createMetricCard("COMPLETED TASKS DELIVERED", lblCompletedVal = new JLabel("0 Orders"), COLOR_TEXT_LIGHT);
        JPanel cardPending = createMetricCard("PENDING BACKLOG ENTRIES", lblPendingVal = new JLabel("0 Tasks"), COLOR_ACCENT);
        JPanel cardSuccess = createMetricCard("ORDER FULFILLMENT RATE", lblSuccessRateVal = new JLabel("100%"), COLOR_ACCENT);

        gridPanel.add(cardEarnings);
        gridPanel.add(cardCompleted);
        gridPanel.add(cardPending);
        gridPanel.add(cardSuccess);

        add(gridPanel, BorderLayout.CENTER);

        // Load baseline data on startup initialization
        refreshLiveMetrics();
    }

    /**
     * Re-queries backend database aggregations and flashes changes onto dashboard viewport cards.
     */
    public void refreshLiveMetrics() {
        HashMap<String, Object> data = bookingDAO.getSellerAnalytics(sellerId);
        
        double earnings = (double) data.get("total_earnings");
        int completed = (int) data.get("completed_jobs");
        int pending = (int) data.get("pending_requests");
        int rate = (int) data.get("success_rate");

        lblEarningsVal.setText(String.format("RM %.2f", earnings));
        lblCompletedVal.setText(completed + " Milestones");
        lblPendingVal.setText(pending + " Requests");
        lblSuccessRateVal.setText(rate + "% Efficiency");
    }

    // --- CARD STRUCTURAL COMPONENT FACTORY ---
    private JPanel createMetricCard(String subtitle, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(valueColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(valueLabel);

        return card;
    }
}