package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.dao.UserDAO;

public class SettingsDialog extends JDialog {
    private int userId;
    private JFrame parentFrame;

    // Palette Design Matrix (Matches your dashboard system)
    private final Color COLOR_BG         = new Color(24, 28, 36);   
    private final Color COLOR_CARD       = new Color(17, 19, 24);   
    private final Color COLOR_ACCENT     = new Color(81, 107, 240);  
    private final Color COLOR_DANGER     = new Color(231, 76, 60);   // Crimson Red for Deactivation
    private final Color COLOR_INPUT_BG   = new Color(32, 38, 50);   
    private final Color COLOR_TEXT_LIGHT = new Color(245, 246, 250); 
    private final Color COLOR_TEXT_MUTED = new Color(140, 147, 171); 
    private final Color COLOR_BORDER     = new Color(45, 52, 71);    

    public SettingsDialog(JFrame parent, int userId) {
        super(parent, "Workspace Control Center — Settings", true);
        this.userId = userId;
        this.parentFrame = parent;

        setSize(500, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        // Main Layout wrapper
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);
        rootPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Modern Customized Tabbed Interface Control panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(COLOR_CARD);
        tabbedPane.setForeground(COLOR_ACCENT);

        // Insert settings modules
        tabbedPane.addTab("Security Keys", createSecurityTab());
//        tabbedPane.addTab("Preferences", createPreferencesTab());
        tabbedPane.addTab("Account Status", createAccountStatusTab());

        rootPanel.add(tabbedPane, BorderLayout.CENTER);
        add(rootPanel);
    }

    // --- 1. SECURITY CONTROL TAB MODULE ---
    private JPanel createSecurityTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Modify Security Access Password");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(COLOR_TEXT_LIGHT);
        gbc.gridy = 0; panel.add(lblTitle, gbc);

        JPasswordField txtNewPass = createCustomPasswordField();
        gbc.gridy = 1; panel.add(createFormLabel("New Secure Password:"), gbc);
        gbc.gridy = 2; panel.add(txtNewPass, gbc);

        JPasswordField txtConfirmPass = createCustomPasswordField();
        gbc.gridy = 3; panel.add(createFormLabel("Confirm New Password:"), gbc);
        gbc.gridy = 4; panel.add(txtConfirmPass, gbc);

        JButton btnSavePassword = new JButton("Update Password Key");
        btnSavePassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSavePassword.setForeground(COLOR_TEXT_LIGHT);
        btnSavePassword.setBackground(COLOR_ACCENT);
        btnSavePassword.setFocusPainted(false);
        btnSavePassword.setBorderPainted(false);
        btnSavePassword.setPreferredSize(new Dimension(0, 36));
        btnSavePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSavePassword.addActionListener(e -> {
            String pass = new String(txtNewPass.getPassword()).trim();
            String confirm = new String(txtConfirmPass.getPassword()).trim();

            if (pass.isEmpty() || confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be blank.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UserDAO userDAO = new UserDAO();
            if (userDAO.updatePassword(userId, pass)) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtNewPass.setText("");
                txtConfirmPass.setText("");
            }
        });

        gbc.gridy = 5; gbc.insets = new Insets(15, 0, 0, 0);
        panel.add(btnSavePassword, gbc);

        return panel;
    }

    // --- 2. EXTRA PREFERENCES BONUS FUNCTION TAB ---
//    private JPanel createPreferencesTab() {
//        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 15));
//        panel.setBackground(COLOR_CARD);
//        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
//
//        JLabel lblTitle = new JLabel("System View Preferences");
//        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
//        lblTitle.setForeground(COLOR_TEXT_LIGHT);
//        panel.add(lblTitle);
//
//        JCheckBox chkSounds = new JCheckBox("Enable Dashboard Alert Audio Chimes (SFX)");
//        chkSounds.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        chkSounds.setForeground(COLOR_TEXT_LIGHT);
//        chkSounds.setOpaque(false);
//        panel.add(chkSounds);
//
//        JCheckBox chkEmail = new JCheckBox("Mirror System Messages to Registered Academic Email");
//        chkEmail.setSelected(true);
//        chkEmail.setFont(new Font("Segoe UI", Font.PLAIN, 12));
//        chkEmail.setForeground(COLOR_TEXT_LIGHT);
//        chkEmail.setOpaque(false);
//        panel.add(chkEmail);
//
//        JButton btnSavePrefs = new JButton("Apply Workspace Configurations");
//        btnSavePrefs.setFont(new Font("Segoe UI", Font.BOLD, 12));
//        btnSavePrefs.setBackground(new Color(45, 52, 71));
//        btnSavePrefs.setForeground(COLOR_TEXT_LIGHT);
//        btnSavePrefs.setBorderPainted(false);
//        btnSavePrefs.setFocusPainted(false);
//        btnSavePrefs.addActionListener(e -> JOptionPane.showMessageDialog(this, "Workspace view parameters localized cleanly."));
//        panel.add(btnSavePrefs);
//
//        return panel;
//    }

    // --- 3. ACCOUNT STATUS TAB MODULE (SOFT DEACTIVATE) ---
    private JPanel createAccountStatusTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblWarningHeader = new JLabel("Danger Management Console Zone", SwingConstants.CENTER);
        lblWarningHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWarningHeader.setForeground(COLOR_DANGER);
        panel.add(lblWarningHeader, BorderLayout.NORTH);

        JTextArea txtWarningDesc = new JTextArea(
            "Deactivating your profile will mask your active service gigs and remove your account records from the public student search terminal indices.\n\n" +
            "Your structural historical transactions, wallet ledgers, and pending order streams are preserved for financial validation audit integrity."
        );
        txtWarningDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtWarningDesc.setForeground(COLOR_TEXT_MUTED);
        txtWarningDesc.setBackground(COLOR_CARD);
        txtWarningDesc.setLineWrap(true);
        txtWarningDesc.setWrapStyleWord(true);
        txtWarningDesc.setEditable(false);
        panel.add(txtWarningDesc, BorderLayout.CENTER);

        JButton btnDeactivate = new JButton("Deactivate My Profile Workspace");
        btnDeactivate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDeactivate.setForeground(COLOR_TEXT_LIGHT);
        btnDeactivate.setBackground(COLOR_DANGER);
        btnDeactivate.setFocusPainted(false);
        btnDeactivate.setBorderPainted(false);
        btnDeactivate.setPreferredSize(new Dimension(0, 42));
        btnDeactivate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnDeactivate.addActionListener(e -> {
            int confirmChoice = JOptionPane.showConfirmDialog(this, 
                "Are you absolutely certain you want to soft-delete your UniVerse profile data? This will terminate your current active session.", 
                "Confirm Final Termination Request", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirmChoice == JOptionPane.YES_OPTION) {
                UserDAO userDAO = new UserDAO();
                if (userDAO.deactivateUserAccount(userId)) {
                    JOptionPane.showMessageDialog(this, "Account successfully soft-deleted. Returning to Authentication terminal.", "Session Terminated", JOptionPane.INFORMATION_MESSAGE);
                    
                    this.dispose();        // Terminate Settings Popup
                    parentFrame.dispose(); // Terminate Main System Dashboard Window
                    
                    // Boot user back to a clean Login Window interface sequence
                    new LoginWindow().setVisible(true);
                }
            }
        });
        panel.add(btnDeactivate, BorderLayout.SOUTH);

        return panel;
    }

    // --- INTERACTIVE FACTORIES ---
    private JLabel createFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(COLOR_TEXT_MUTED);
        return l;
    }

    private JPasswordField createCustomPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setPreferredSize(new Dimension(0, 36));
        f.setBackground(COLOR_INPUT_BG);
        f.setForeground(COLOR_TEXT_LIGHT);
        f.setCaretColor(COLOR_TEXT_LIGHT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        return f;
    }
}