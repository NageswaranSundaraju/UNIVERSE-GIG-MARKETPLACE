package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginWindow extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegisterRedirect;

    // Styled Palette Design Matrix (Matches MainDashboard perfectly)
    private final Color COLOR_BG           = new Color(24, 28, 36);   // Dark Slate Base
    private final Color COLOR_CARD         = new Color(17, 19, 24);   // Deep Onyx Inner Container
    private final Color COLOR_ACCENT       = new Color(81, 107, 240);  // Indigo Premium Brand Highlight
    private final Color COLOR_INPUT_BG     = new Color(32, 38, 50);   // Distinct Dark Input Field Canvas
    private final Color COLOR_TEXT_LIGHT   = new Color(245, 246, 250); // Crisp Off-White Header Text
    private final Color COLOR_TEXT_MUTED   = new Color(140, 147, 171); // Soft Slate Gray Form Labels
    private final Color COLOR_BORDER       = new Color(45, 52, 71);    // Subtle input divider line

    public LoginWindow() {
        setTitle("UniVerse Gig Marketplace - Authentication");
        setSize(450, 420); // Slightly expanded to add elegant premium spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen
        setResizable(false);

        // Underlying frame layout container modification
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // Inner Card Panel to containerize form elements neatly
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(COLOR_CARD);
        cardPanel.setBorder(new EmptyBorder(30, 35, 30, 35)); // Spacious internal canvas padding
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Vertical layout separation row spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // --- TITLE BRAND HEADER ---
        JLabel lblTitle = new JLabel("UNIVERSE", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        cardPanel.add(lblTitle, gbc);

        JLabel lblSubTitle = new JLabel("CAMPUS GIG AUTHENTICATION", SwingConstants.CENTER);
        lblSubTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSubTitle.setForeground(COLOR_ACCENT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0); // Pushes up title group away from input rows
        cardPanel.add(lblSubTitle, gbc);

        // Reset tracking spacing for input fields layout
        gbc.gridwidth = 1;
        gbc.insets = new Insets(6, 0, 6, 0);

        // --- USERNAME COMPONENT ROW ---
        JLabel lblUsername = new JLabel("Username / Student ID");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUsername.setForeground(COLOR_TEXT_MUTED);
        gbc.gridy = 2; gbc.gridx = 0;
        cardPanel.add(lblUsername, gbc);

        txtUsername = createStyledInputField();
        gbc.gridy = 3;
        cardPanel.add(txtUsername, gbc);

        // --- PASSWORD COMPONENT ROW ---
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPassword.setForeground(COLOR_TEXT_MUTED);
        gbc.gridy = 4;
        cardPanel.add(lblPassword, gbc);

        txtPassword = createStyledPasswordField();
        gbc.gridy = 5;
        cardPanel.add(txtPassword, gbc);

        // --- ACTION CONTROLS BUTTONS LAYOUT ---
        btnLogin = new JButton("Login Workspace");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setForeground(COLOR_TEXT_LIGHT);
        btnLogin.setBackground(COLOR_ACCENT);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 40)); // Thicker interactive profile tap boundary

        // Interactive primary button hover state
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(COLOR_ACCENT.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btnLogin.setBackground(COLOR_ACCENT); }
        });

        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 5, 0); // Extra upper padding to split input from submission actions
        cardPanel.add(btnLogin, gbc);

        // Alternative Redirect Link Style Setup
        btnRegisterRedirect = new JButton("Create an Account");
        btnRegisterRedirect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegisterRedirect.setForeground(COLOR_TEXT_MUTED);
        btnRegisterRedirect.setContentAreaFilled(false);
        btnRegisterRedirect.setBorderPainted(false);
        btnRegisterRedirect.setFocusPainted(false);
        btnRegisterRedirect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRegisterRedirect.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnRegisterRedirect.setForeground(COLOR_TEXT_LIGHT); }
            @Override
            public void mouseExited(MouseEvent e) { btnRegisterRedirect.setForeground(COLOR_TEXT_MUTED); }
        });

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(btnRegisterRedirect, gbc);

        // Frame configuration assembly 
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(cardPanel);

        add(wrapperPanel, BorderLayout.CENTER);
    }

    // --- CUSTOM FIELD GRAPHICS COMPONENT FACTORIES ---

    private JTextField createStyledInputField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(340, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(COLOR_INPUT_BG);
        field.setForeground(COLOR_TEXT_LIGHT);
        field.setCaretColor(COLOR_TEXT_LIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10) // Smooth padding inside the text bounds
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(340, 36));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(COLOR_INPUT_BG);
        field.setForeground(COLOR_TEXT_LIGHT);
        field.setCaretColor(COLOR_TEXT_LIGHT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        return field;
    }

    // Input Getters to pass raw strings to the Controller safely
    public String getUsernameInput() { return txtUsername.getText().trim(); }
    public String getPasswordInput() { return new String(txtPassword.getPassword()); }

    // Event Listener assignments managed by your Controller later
    public void addLoginListener(ActionListener listener) { btnLogin.addActionListener(listener); }
    public void addRedirectListener(ActionListener listener) { btnRegisterRedirect.addActionListener(listener); }
}