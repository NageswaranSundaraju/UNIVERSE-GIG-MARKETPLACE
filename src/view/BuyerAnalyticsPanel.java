package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.dao.BookingDAO;

public class BuyerAnalyticsPanel extends JPanel {
    private int buyerId;
    private BookingDAO bookingDAO;
    
    private JLabel lblBalanceVal;
    private JLabel lblExpenseVal;

    public BuyerAnalyticsPanel(int buyerId) {
        this.buyerId = buyerId;
        this.bookingDAO = new BookingDAO();
        
        // Colors matching your design matrix
        Color colorBg = new Color(24, 28, 36);
        Color colorCard = new Color(32, 38, 50);
        Color colorTextLight = new Color(245, 246, 250);
        Color colorTextMuted = new Color(140, 147, 171);
        Color colorAccent = new Color(81, 107, 240);
        Color colorGreen = new Color(46, 204, 113);

        setLayout(new BorderLayout());
        setBackground(colorBg);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Header Section
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("📊 Buyer Expense Engine Statistics Matrix");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(colorTextLight);
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Grid Layout for Summary Metric Cards
        JPanel metricsGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Card 1: Wallet Balance Allocation Panel
        JPanel cardBalance = createMetricCard("AVAILABLE WALLET CAPITAL", colorCard, colorTextMuted);
        lblBalanceVal = new JLabel("RM 0.00");
        lblBalanceVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBalanceVal.setForeground(colorGreen);
        lblBalanceVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardBalance.add(Box.createRigidArea(new Dimension(0, 15)));
        cardBalance.add(lblBalanceVal);

        // Card 2: Total Spent Accumulated Metrics Panel
        JPanel cardExpenses = createMetricCard("TOTAL LIFETIME EXPENDITURE", colorCard, colorTextMuted);
        lblExpenseVal = new JLabel("RM 0.00");
        lblExpenseVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblExpenseVal.setForeground(colorAccent);
        lblExpenseVal.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardExpenses.add(Box.createRigidArea(new Dimension(0, 15)));
        cardExpenses.add(lblExpenseVal);

        metricsGrid.add(cardBalance);
        metricsGrid.add(cardExpenses);

        // Bottom Decorative Workspace Area for Analytics Graphing
        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.setBackground(colorCard);
        lowerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(45, 52, 71), 1),
            new EmptyBorder(30, 30, 30, 30)
        ));
        
        JLabel lblGraphPlaceholder = new JLabel("💡 Operational Insight: Keep track of your local campus purchases and transaction receipts live.", SwingConstants.CENTER);
        lblGraphPlaceholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblGraphPlaceholder.setForeground(colorTextMuted);
        lowerPanel.add(lblGraphPlaceholder, BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(metricsGrid, BorderLayout.NORTH);
        centerContainer.add(lowerPanel, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);
        
        // Load Live Numeric Data Matrices
        refreshMetrics();
    }

    public void refreshMetrics() {
        double currentBalance = bookingDAO.getBuyerWalletBalance(buyerId);
        double totalSpent = bookingDAO.getBuyerTotalExpenses(buyerId);

        lblBalanceVal.setText(String.format("RM %.2f", currentBalance));
        lblExpenseVal.setText(String.format("RM %.2f", totalSpent));
    }

    private JPanel createMetricCard(String title, Color bgColor, Color textColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(45, 52, 71), 1),
            new EmptyBorder(25, 20, 25, 20)
        ));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(textColor);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitle);
        
        return card;
    }
}