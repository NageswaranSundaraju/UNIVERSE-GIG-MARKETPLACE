package view;

import model.dao.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ProfileDialog extends JDialog {
    private UserDAO userDAO = new UserDAO();
    private int userId;
    private String role;
    private JLabel lblWalletOrPoints;
    private JLabel lblExtraInfo; // Displays Tier or Address

    public ProfileDialog(JFrame parent, int userId, String role) {
        super(parent, "My Profile Matrix", true);
        this.userId = userId;
        this.role = role;

        setSize(400, 320);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fetch current live metrics from database
        Map<String, Object> data = role.equalsIgnoreCase("BUYER") ? 
                                   userDAO.getBuyerProfile(userId) : userDAO.getSellerProfile(userId);

        // Common Fields
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(new JLabel("Username:"), gbc);
        JLabel lblName = new JLabel(String.valueOf(data.getOrDefault("username", "Unknown")));
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1; contentPanel.add(lblName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridx = 1; contentPanel.add(new JLabel(String.valueOf(data.getOrDefault("email", "-"))), gbc);

        // Role-Specific Field Mapping
        gbc.gridx = 0; gbc.gridy = 2;
        lblWalletOrPoints = new JLabel();
        contentPanel.add(lblWalletOrPoints, gbc);
        
        JLabel lblValue = new JLabel();
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1; contentPanel.add(lblValue, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        lblExtraInfo = new JLabel();
        contentPanel.add(lblExtraInfo, gbc);
        
        JLabel lblExtraValue = new JLabel();
        gbc.gridx = 1; contentPanel.add(lblExtraValue, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Conditional Action Button Layout Setups
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        
        if (role.equalsIgnoreCase("BUYER")) {
            lblWalletOrPoints.setText("Wallet Balance:");
            lblValue.setText(String.format("RM %.2f", data.get("wallet_balance")));
            lblValue.setForeground(new Color(46, 204, 113));
            
            lblExtraInfo.setText("Deliver To:");
            lblExtraValue.setText(data.get("delivery_address") != null ? String.valueOf(data.get("delivery_address")) : "No Address Set");

            JButton btnTopUp = new JButton("Top-Up Wallet Balance");
            btnTopUp.setBackground(new Color(52, 152, 219));
            btnTopUp.setForeground(Color.WHITE);
            
            btnTopUp.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(this, "Enter top-up amount (RM):", "Add Funds", JOptionPane.QUESTION_MESSAGE);
                if (input != null && !input.trim().isEmpty()) {
                    try {
                        double amount = Double.parseDouble(input);
                        if (amount <= 0) throw new NumberFormatException();
                        
                        if (userDAO.topUpBuyerWallet(userId, amount)) {
                            JOptionPane.showMessageDialog(this, "RM " + String.format("%.2f", amount) + " added successfully!");
                            // Instantly refresh window view text
                            Map<String, Object> updated = userDAO.getBuyerProfile(userId);
                            lblValue.setText(String.format("RM %.2f", updated.get("wallet_balance")));
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid positive numeric amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            actionPanel.add(btnTopUp);
        } else {
            // Seller View Mapping
            lblWalletOrPoints.setText("Service Experience:");
            lblValue.setText(data.get("service_points") + " XP Points");
            lblValue.setForeground(new Color(155, 89, 182));

            lblExtraInfo.setText("Current Tier Rank:");
            lblExtraValue.setText(String.valueOf(data.getOrDefault("tier_name", "BRONZE")));
            lblExtraValue.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblExtraValue.setForeground(new Color(241, 196, 15)); // Golden rank aesthetic text color
        }

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        actionPanel.add(btnClose);
        add(actionPanel, BorderLayout.SOUTH);
    }
}