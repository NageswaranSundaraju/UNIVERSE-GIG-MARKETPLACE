package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegisterRedirect;

    public LoginWindow() {
        // Window properties
        setTitle("UniVerse Gig Marketplace - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen
        setResizable(false);

        // Main Layout Container
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Header
        JLabel lblTitle = new JLabel("UniVerse Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Username Field
        JLabel lblUsername = new JLabel("Username / Student ID:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(txtUsername, gbc);

        // Password Field
        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(txtPassword, gbc);

        // Action Buttons Row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btnRegisterRedirect = new JButton("Create Account");
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegisterRedirect);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    // Input Getters to pass raw strings to the Controller safely
    public String getUsernameInput() { return txtUsername.getText().trim(); }
    public String getPasswordInput() { return new String(txtPassword.getPassword()); }

    // Event Listener assignments managed by your Controller later
    public void addLoginListener(ActionListener listener) { btnLogin.addActionListener(listener); }
    public void addRedirectListener(ActionListener listener) { btnRegisterRedirect.addActionListener(listener); }
}