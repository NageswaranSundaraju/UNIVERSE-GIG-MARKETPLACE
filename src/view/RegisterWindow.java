package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class RegisterWindow extends JFrame {
    private JTextField txtUsername;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> cmbRole;
    private JButton btnSubmitRegister;
    private JButton btnBackToLogin;

    public RegisterWindow() {
        setTitle("UniVerse Gig Marketplace - Registration");
        setSize(450, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Header
        JLabel lblTitle = new JLabel("Create Campus Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        // Fields Setup
        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(new JLabel("Choose Username:"), gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1; mainPanel.add(txtUsername, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 2; mainPanel.add(new JLabel("Campus Email:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 1; mainPanel.add(txtEmail, gbc);

        // Role Selector Dropdown
        gbc.gridx = 0; gbc.gridy = 3; mainPanel.add(new JLabel("Account Type:"), gbc);
        cmbRole = new JComboBox<>(new String[]{"BUYER", "SELLER"});
        gbc.gridx = 1; mainPanel.add(cmbRole, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 4; mainPanel.add(new JLabel("Password:"), gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1; mainPanel.add(txtPassword, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 5; mainPanel.add(new JLabel("Confirm Password:"), gbc);
        txtConfirmPassword = new JPasswordField(15);
        gbc.gridx = 1; mainPanel.add(txtConfirmPassword, gbc);

        // Action Buttons Row
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnSubmitRegister = new JButton("Register Account");
        btnSubmitRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btnBackToLogin = new JButton("Back to Login");

        buttonPanel.add(btnSubmitRegister);
        buttonPanel.add(btnBackToLogin);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 6, 6, 6);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    // Input Getters
    public String getUsernameInput() { return txtUsername.getText().trim(); }
    public String getEmailInput() { return txtEmail.getText().trim(); }
    public String getSelectedRole() { return cmbRole.getSelectedItem().toString(); }
    public String getPasswordInput() { return new String(txtPassword.getPassword()); }
    public String getConfirmPasswordInput() { return new String(txtConfirmPassword.getPassword()); }

    // Event Listeners
    public void addSubmitRegisterListener(ActionListener listener) { btnSubmitRegister.addActionListener(listener); }
    public void addBackToLoginListener(ActionListener listener) { btnBackToLogin.addActionListener(listener); }
}