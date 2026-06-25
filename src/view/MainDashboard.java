package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import controller.BookingController;

public class MainDashboard extends JFrame {
    private int currentUserId; 
    private String userRole;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Sidebar Navigation Buttons
    private JButton btnTab1; 
    private JButton btnTab2; 
    private JButton btnAnalytics; 
    private JButton btnLogout;

    // Controllers
    private controller.BuyerLogController buyerLogController;
    private controller.SellerRequestsController sellerRequestsController;
    private controller.SellerActiveJobsController sellerActiveJobsController;
    private view.BuyerAnalyticsPanel buyerAnalytics;

    // Styled Palette Design Matrix
    private final Color COLOR_PRIMARY_DARK = new Color(24, 28, 36);   // Dark Slate Dashboard Base
    private final Color COLOR_SIDEBAR      = new Color(17, 19, 24);   // Deep Onyx Sidebar Accent
    private final Color COLOR_ACCENT       = new Color(81, 107, 240);  // Indigo Premium Brand Highlight
    private final Color COLOR_TEXT_LIGHT   = new Color(245, 246, 250); // Crisp Off-White Text
    private final Color COLOR_TEXT_MUTED   = new Color(140, 147, 171); // Soft Slate Gray Muted Subtext
    private final Color COLOR_CARD_BG      = new Color(32, 38, 50);   // Distinct Inner Canvas Panels

    public MainDashboard(int userId, String role) {
        this.currentUserId = userId; 
        this.userRole = role;

        setTitle("UniVerse Workspace — Dashboard [" + role + "]");
        setSize(1100, 720); // Bounded dimensions for a spacious dashboard grid
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Frame layout setup
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_PRIMARY_DARK);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Soft modern canvas outer padding

        // Fetch target user profile display identity metadata
        model.dao.UserDAO userDAO = new model.dao.UserDAO();
        String activeUsername = "User";
        try {
            if ("BUYER".equalsIgnoreCase(role)) {
                activeUsername = String.valueOf(userDAO.getBuyerProfile(userId).getOrDefault("username", "Buyer"));
            } else {
                activeUsername = String.valueOf(userDAO.getSellerProfile(userId).getOrDefault("username", "Seller"));
            }
        } catch (Exception e) {
            activeUsername = role; // Safe structural fallback
        }

        // Build clean dashboard regions
        createHeaderBar(activeUsername);
        setupSidebar();
        setupWorkspaces();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(COLOR_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setBorder(new EmptyBorder(30, 15, 30, 15));

        // Profile Identity Core Badge Container
        JLabel lblRole = new JLabel(userRole.toUpperCase() + " SPACE");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRole.setForeground(COLOR_ACCENT);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(lblRole);
        
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 35)));

        // Context-Aware Tab Initializations based on Identity Access Matrix
        if ("BUYER".equalsIgnoreCase(userRole)) {
            btnTab1 = createStyledSidebarButton("Marketplace Search");
            btnTab2 = createStyledSidebarButton("My Bookings Log");
        } else {
            btnTab1 = createStyledSidebarButton("My Service Gigs");
            btnTab2 = createStyledSidebarButton("Incoming Requests");
        }

        btnAnalytics = createStyledSidebarButton("Analytics Engine");
        btnLogout = createStyledSidebarButton("Disconnect Log");

        // Inject functional layout stack down the sidebar
        JButton[] navigationTabs = {btnTab1, btnTab2, btnAnalytics};
        for (JButton tab : navigationTabs) {
            sidebarPanel.add(tab);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        // Push Logout button to the very bottom row of the frame anchor
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);
        
        // Mark first navigation button active by default style constraints
        setTabSelectedStyle(btnTab1);
    }

    private void setupWorkspaces() {
        if ("BUYER".equalsIgnoreCase(userRole)) {
            BuyerSearchPanel buyerSearch = new BuyerSearchPanel();
            buyerSearch.setBackground(COLOR_PRIMARY_DARK);
            new controller.MarketplaceController(buyerSearch, new model.dao.GigDAO(), this);
            contentPanel.add(buyerSearch, "TAB_ONE");
            
            BuyerLogPanel logPanel = new BuyerLogPanel();
            logPanel.setBackground(COLOR_PRIMARY_DARK);
            buyerLogController = new controller.BuyerLogController(logPanel, new model.dao.BookingDAO(), currentUserId);
            contentPanel.add(logPanel, "TAB_TWO");
            
        } else {
            SellerGigPanel sellerGigs = new SellerGigPanel();
            sellerGigs.setBackground(COLOR_PRIMARY_DARK);
            new controller.SellerGigController(sellerGigs, new model.dao.GigDAO(), currentUserId);
            contentPanel.add(sellerGigs, "TAB_ONE");
            
            // Refactored dual operational sub-view table stack
            JPanel sellerOperationsDashboard = new JPanel(new GridLayout(2, 1, 20, 20));
            sellerOperationsDashboard.setOpaque(false);
            
            SellerRequestsPanel requestsPanel = new SellerRequestsPanel();
            requestsPanel.setBackground(COLOR_CARD_BG);
            sellerRequestsController = new controller.SellerRequestsController(requestsPanel, new model.dao.BookingDAO(), currentUserId);
            sellerOperationsDashboard.add(requestsPanel);
            
            SellerActiveJobsPanel activeJobsPanel = new SellerActiveJobsPanel();
            activeJobsPanel.setBackground(COLOR_CARD_BG);
            sellerActiveJobsController = new controller.SellerActiveJobsController(activeJobsPanel, new model.dao.BookingDAO(), currentUserId);
            sellerOperationsDashboard.add(activeJobsPanel);
            
            contentPanel.add(sellerOperationsDashboard, "TAB_TWO");
        }
        
        // Analytics Canvas Interface Base Card
     // --- FIND THIS AT THE BOTTOM OF setupWorkspaces() METHOD ---
        // Old code: JPanel card3 = new JPanel(new BorderLayout()); ...
        
        // REPLACE WITH THIS CLEAN INSTANCE LINK:
        if (!"BUYER".equalsIgnoreCase(userRole)) {
            // For Sellers, load our new live data Analytics Engine Panel
            SellerAnalyticsPanel sellerAnalytics = new SellerAnalyticsPanel(currentUserId);
            contentPanel.add(sellerAnalytics, "ANALYTICS");
        } else {
        	this.buyerAnalytics = new BuyerAnalyticsPanel(currentUserId);
            contentPanel.add(buyerAnalytics, "ANALYTICS");
            
        }
    }

    private void createHeaderBar(String username) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_SIDEBAR); 
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // App Logo Icon Structure
        JLabel lblLogo = new JLabel("UNIVERSE");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(COLOR_TEXT_LIGHT);
        
        JLabel lblSubLogo = new JLabel("  CAMPUS MARKETPLACE");
        lblSubLogo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSubLogo.setForeground(COLOR_TEXT_MUTED);

        JPanel logoGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoGroup.setOpaque(false);
        logoGroup.add(lblLogo);
        logoGroup.add(lblSubLogo);
        headerPanel.add(logoGroup, BorderLayout.WEST);

        // Control Toolbar
        JPanel controlsBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlsBar.setOpaque(false);

        // Header Control Buttons Custom Factory Method Styling
        JButton btnBell = createHeaderActionButton("[!] Alerts", false);
        btnBell.addActionListener(e -> {
            model.dao.NotificationDAO notifyDAO = new model.dao.NotificationDAO();
            java.util.ArrayList<String> notices = notifyDAO.fetchUserNotifications(currentUserId);
            
            if (notices.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Inbox clean! No new system logs.", "Alert Notification Logs", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder sb = new StringBuilder();
                for (String msg : notices) {
                    sb.append(msg).append("\n\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Alert Center Hub", JOptionPane.PLAIN_MESSAGE);
            }
        });
        controlsBar.add(btnBell);

        // ===================================================================
        // ADD THE SETTINGS BUTTON HERE (Between Alerts and Profile)
        // ===================================================================
        JButton btnSettings = createHeaderActionButton("[*] Settings", false);
        btnSettings.addActionListener(e -> {
            SettingsDialog settingsBox = new SettingsDialog(this, currentUserId);
            settingsBox.setVisible(true);
        });
        controlsBar.add(btnSettings);
        // ===================================================================

        JButton btnProfile = createHeaderActionButton("Account: " + username, true);
        btnProfile.addActionListener(evt -> {
            ProfileDialog profileDialog = new ProfileDialog(this, currentUserId, userRole);
            profileDialog.setVisible(true);
        });
        controlsBar.add(btnProfile);

        headerPanel.add(controlsBar, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }
    // --- MODERN COMPONENT GRAPHICS FACTORIES ---

    private JButton createStyledSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(210, 42));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(COLOR_TEXT_MUTED);
        button.setBackground(COLOR_SIDEBAR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Interactive UI State Hover Feedback
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getForeground() != COLOR_TEXT_LIGHT) {
                    button.setForeground(COLOR_TEXT_LIGHT);
                    button.setBackground(new Color(28, 32, 40));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getForeground() != COLOR_TEXT_LIGHT) {
                    button.setForeground(COLOR_TEXT_MUTED);
                    button.setBackground(COLOR_SIDEBAR);
                }
            }
        });
        return button;
    }

    private JButton createHeaderActionButton(String text, boolean highlight) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setOpaque(true);

        if (highlight) {
            button.setBackground(COLOR_ACCENT);
            button.setForeground(COLOR_TEXT_LIGHT);
            button.setBorderPainted(false);
        } else {
            button.setBackground(COLOR_SIDEBAR);
            button.setForeground(COLOR_TEXT_MUTED);
            button.setBorder(BorderFactory.createLineBorder(new Color(45, 52, 71), 1));
        }
        return button;
    }

    private void setTabSelectedStyle(JButton selectedTab) {
        JButton[] allTabs = {btnTab1, btnTab2, btnAnalytics};
        for (JButton t : allTabs) {
            if (t != null) {
                t.setForeground(COLOR_TEXT_MUTED);
                t.setBackground(COLOR_SIDEBAR);
            }
        }
        selectedTab.setForeground(COLOR_TEXT_LIGHT);
        selectedTab.setBackground(COLOR_ACCENT); // Selected navigation focus token indicator state
    }

    // --- ROUTING WRAPPERS ---

    public void switchView(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }

    public void addNavigationListeners(ActionListener tab1, ActionListener tab2, ActionListener analytics, ActionListener logout) {
        btnTab1.addActionListener(e -> { setTabSelectedStyle(btnTab1); tab1.actionPerformed(e); });
        btnTab2.addActionListener(e -> { setTabSelectedStyle(btnTab2); tab2.actionPerformed(e); });
        btnAnalytics.addActionListener(e -> { setTabSelectedStyle(btnAnalytics); analytics.actionPerformed(e); });
        btnAnalytics.addActionListener(e -> { 
            setTabSelectedStyle(btnAnalytics); 
            
            // If we are logged in as a buyer and the panel has loaded, pull live data counters!
            if (this.buyerAnalytics != null) {
                this.buyerAnalytics.refreshMetrics();
            }
            
            analytics.actionPerformed(e); 
        });
        btnLogout.addActionListener(logout);
    }

    public void openCheckoutWindow(int gigId, String title, double price) {
        CheckoutWindow checkoutWindow = new CheckoutWindow();
        checkoutWindow.setGigDetails(gigId, title, price);
        
        // Fix spelling to 'checkoutWindow' 
        // Access your local log panel instance safely through your log controller reference
        view.BuyerLogPanel logPanelView = null;
        if (this.buyerLogController != null) {
            // Pulling the active view panel directly from our initialized controller stack
            logPanelView = (view.BuyerLogPanel) SwingUtilities.getAncestorOfClass(view.BuyerLogPanel.class, contentPanel); 
        }
        
        new BookingController(checkoutWindow, new model.dao.BookingDAO(), currentUserId, logPanelView);
        checkoutWindow.setVisible(true);
    }
    
    public controller.BuyerLogController getBuyerLogController() { return this.buyerLogController; }
    public controller.SellerActiveJobsController getSellerActiveJobsController() { return this.sellerActiveJobsController; }
    public controller.SellerRequestsController getSellerRequestsController() { return this.sellerRequestsController; }
}