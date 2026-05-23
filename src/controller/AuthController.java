package controller;

import model.dao.UserDAO;
import view.LoginWindow;
import view.RegisterWindow;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthController {
    private LoginWindow loginWindow;
    private RegisterWindow registerWindow;
    private UserDAO userDAO;

    public AuthController(LoginWindow loginWindow, RegisterWindow registerWindow, UserDAO userDAO) {
        this.loginWindow = loginWindow;
        this.registerWindow = registerWindow;
        this.userDAO = userDAO;

        // Attach listeners to the Login Window buttons
        this.loginWindow.addLoginListener(new LoginButtonListener());
        this.loginWindow.addRedirectListener(new RedirectToRegisterListener());

        // Attach listeners to the Register Window buttons
        this.registerWindow.addSubmitRegisterListener(new SubmitRegisterListener());
        this.registerWindow.addBackToLoginListener(new RedirectToLoginListener());
    }

    // --- LOGIN WINDOW LISTENERS ---
    
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginWindow.getUsernameInput();
            String password = loginWindow.getPasswordInput();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginWindow, "Please fill in all fields.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Authenticate against database records
            String role = userDAO.authenticateUser(username, password);

            if (role != null) {
                JOptionPane.showMessageDialog(loginWindow, "Login Successful! Welcome back as a " + role + ".");
                loginWindow.dispose(); // Close login panel
                
                // TODO: Launch your MainDashboard(role) panel here next week!
                System.out.println("Redirecting to " + role + " Dashboard...");
            } else {
                JOptionPane.showMessageDialog(loginWindow, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RedirectToRegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loginWindow.setVisible(false);
            registerWindow.setVisible(true); // Switch screens
        }
    }

    // --- REGISTRATION WINDOW LISTENERS ---

    private class SubmitRegisterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = registerWindow.getUsernameInput();
            String email = registerWindow.getEmailInput();
            String role = registerWindow.getSelectedRole();
            String password = registerWindow.getPasswordInput();
            String confirmPassword = registerWindow.getConfirmPasswordInput();

            // Simple Field Validations
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerWindow, "All fields are mandatory.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerWindow, "Passwords do not match!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Execute database creation script via DAO
            boolean success = userDAO.registerUser(username, email, password, role);

            if (success) {
                JOptionPane.showMessageDialog(registerWindow, "Account created successfully! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                registerWindow.setVisible(false);
                loginWindow.setVisible(true); // Direct user back to log in
            } else {
                JOptionPane.showMessageDialog(registerWindow, "Registration failed. Username or Email might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class RedirectToLoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            registerWindow.setVisible(false);
            loginWindow.setVisible(true); // Switch back
        }
    }
}