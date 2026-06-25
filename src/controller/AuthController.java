package controller;

import view.*;
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

		this.loginWindow.addLoginListener(new LoginButtonListener());
		this.loginWindow.addRedirectListener(new RedirectToRegisterListener());

		this.registerWindow.addSubmitRegisterListener(new SubmitRegisterListener());
		this.registerWindow.addBackToLoginListener(new RedirectToLoginListener());
	}

	private class LoginButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String username = loginWindow.getUsernameInput();
			String password = loginWindow.getPasswordInput();

			if (username.isEmpty() || password.isEmpty()) {
				JOptionPane.showMessageDialog(loginWindow, "Please fill in all fields.", "Error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			model.dao.AdminDAO adminDAO = new model.dao.AdminDAO();
			if (adminDAO.authenticateAdmin(username, password)) {
				JOptionPane.showMessageDialog(loginWindow, "System Administrator Authenticated.", "Admin Login",
						JOptionPane.INFORMATION_MESSAGE);
				loginWindow.dispose();

				new view.AdminDashboard().setVisible(true);

				return;
			}

			int loggedInUserId = userDAO.authenticateUser(username, password);

			if (loggedInUserId == -2) {
				JOptionPane.showMessageDialog(loginWindow,
						"Account is deactivated, please contact administrator to unlock.", "Account Suspended",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			else if (loggedInUserId != -1) {

				String role = userDAO.getUserRoleById(loggedInUserId);

				JOptionPane.showMessageDialog(loginWindow, "Login Successful! Welcome.");
				loginWindow.dispose();

				MainDashboard dashboard = new MainDashboard(loggedInUserId, role);

				// Attach the navigation routing mechanics
				dashboard.addNavigationListeners(evt -> dashboard.switchView("TAB_ONE"), evt -> {
					if (dashboard.getBuyerLogController() != null) {
						dashboard.getBuyerLogController().refreshLogTable();
					}
					if (dashboard.getSellerRequestsController() != null) {
						dashboard.getSellerRequestsController().refreshRequestsTable();
					}
					if (dashboard.getSellerActiveJobsController() != null) {
						dashboard.getSellerActiveJobsController().refreshActiveJobsTable();
					}
					dashboard.switchView("TAB_TWO");
				}, evt -> dashboard.switchView("ANALYTICS"), evt -> {
					dashboard.dispose();
					new AuthController(new LoginWindow(), new RegisterWindow(), new UserDAO()).loginWindow
							.setVisible(true);
				});

				dashboard.setVisible(true);
			}

			else {
				JOptionPane.showMessageDialog(loginWindow, "Invalid credentials.", "Login Failed",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class RedirectToRegisterListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			loginWindow.setVisible(false);
			registerWindow.setVisible(true);
		}
	}

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
				JOptionPane.showMessageDialog(registerWindow, "All fields are mandatory.", "Validation Error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (!password.equals(confirmPassword)) {
				JOptionPane.showMessageDialog(registerWindow, "Passwords do not match!", "Validation Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			boolean success = userDAO.registerUser(username, email, password, role);

			if (success) {
				JOptionPane.showMessageDialog(registerWindow, "Account created successfully! Please log in.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
				registerWindow.setVisible(false);
				loginWindow.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(registerWindow,
						"Registration failed. Username or Email might already exist.", "Database Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class RedirectToLoginListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			registerWindow.setVisible(false);
			loginWindow.setVisible(true);
		}
	}
}