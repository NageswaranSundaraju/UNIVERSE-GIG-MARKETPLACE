package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CheckoutWindow extends JFrame {
    private JLabel lblGigTitle;
    private JLabel lblBasePrice;
    private JSpinner spinQuantity;
    private JCheckBox chkExpress;
    private JLabel lblTotalEstimate;
    private JButton btnCalculate;
    private JButton btnConfirmBooking;
    
    private int selectedGigId;
    private double basePriceAmount;

    // Styled Palette Design Matrix (Matches your premium UI design system)
    private final Color COLOR_BG           = new Color(24, 28, 36);   // Dark Slate Base
    private final Color COLOR_CARD_BG      = new Color(17, 19, 24);   // Deep Onyx Inner Container
    private final Color COLOR_ACCENT       = new Color(81, 107, 240);  // Indigo Premium Brand Highlight
    private final Color COLOR_INPUT_BG     = new Color(32, 38, 50);   // Dark Input Field Canvas
    private final Color COLOR_TEXT_LIGHT   = new Color(245, 246, 250); // Crisp Off-White Core Text
    private final Color COLOR_TEXT_MUTED   = new Color(140, 147, 171); // Soft Slate Gray Form Labels
    private final Color COLOR_BORDER       = new Color(45, 52, 71);    // Subtle Layout Gridlines
    private final Color COLOR_SUCCESS      = new Color(46, 204, 113);  // Dynamic Green for Financial Totals

    public CheckoutWindow() {
        setTitle("Secure Checkout — Review Service Agreement");
        setSize(520, 480); // Adjusted spacing metrics for optimal breathing room
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setResizable(false);

        // Frame layout setup
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // --- INNER FORM CARD ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(25, 30, 25, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Service Title Header
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Selected Service:"), gbc);
        
        lblGigTitle = new JLabel("None Selected");
        lblGigTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGigTitle.setForeground(COLOR_TEXT_LIGHT);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(lblGigTitle, gbc);

        // Row 1: Base Rate
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Base Rate:"), gbc);
        
        lblBasePrice = new JLabel("RM 0.00");
        lblBasePrice.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBasePrice.setForeground(COLOR_TEXT_LIGHT);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(lblBasePrice, gbc);

        // Row 2: Quantity Spinner Component
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Quantity / Units:"), gbc);
        
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spinQuantity.setPreferredSize(new Dimension(100, 32));
        spinQuantity.setFont(new Font("Segoe UI", Font.BOLD, 13));
        spinQuantity.getEditor().getComponent(0).setBackground(COLOR_INPUT_BG);
        spinQuantity.getEditor().getComponent(0).setForeground(COLOR_TEXT_LIGHT);
        spinQuantity.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(spinQuantity, gbc);

        // Row 3: Delivery Speed Checkbox Option
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Delivery Speed:"), gbc);
        
        chkExpress = new JCheckBox("Express Delivery (+30% Premium)");
        chkExpress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkExpress.setForeground(COLOR_TEXT_LIGHT);
        chkExpress.setOpaque(false);
        chkExpress.setFocusPainted(false);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(chkExpress, gbc);

        // Row 4: Pricing Estimate
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        formPanel.add(createFormLabel("Estimated Total:"), gbc);
        
        lblTotalEstimate = new JLabel("RM 0.00");
        lblTotalEstimate.setFont(new Font("Segoe UI", Font.BOLD, 20)); // Enhanced visibility hierarchy scale
        lblTotalEstimate.setForeground(COLOR_SUCCESS);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(lblTotalEstimate, gbc);

        // Add centered padded form card wrapper
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(formPanel);
        add(wrapperPanel, BorderLayout.CENTER);

        // --- BOTTOM BUTTONS BAR ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actionPanel.setOpaque(false);
        
        btnCalculate = new JButton("Recalculate");
        btnCalculate.setPreferredSize(new Dimension(120, 38));
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalculate.setForeground(COLOR_TEXT_LIGHT);
        btnCalculate.setBackground(new Color(45, 52, 71));
        btnCalculate.setFocusPainted(false);
        btnCalculate.setBorderPainted(false);
        btnCalculate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCalculate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnCalculate.setBackground(new Color(58, 67, 92)); }
            @Override
            public void mouseExited(MouseEvent e) { btnCalculate.setBackground(new Color(45, 52, 71)); }
        });

        btnConfirmBooking = new JButton("Place Order");
        btnConfirmBooking.setPreferredSize(new Dimension(130, 38));
        btnConfirmBooking.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnConfirmBooking.setForeground(COLOR_TEXT_LIGHT);
        btnConfirmBooking.setBackground(COLOR_ACCENT);
        btnConfirmBooking.setFocusPainted(false);
        btnConfirmBooking.setBorderPainted(false);
        btnConfirmBooking.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnConfirmBooking.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnConfirmBooking.setBackground(COLOR_ACCENT.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btnConfirmBooking.setBackground(COLOR_ACCENT); }
        });
        
        actionPanel.add(btnCalculate);
        actionPanel.add(btnConfirmBooking);
        add(actionPanel, BorderLayout.SOUTH);
    }

    // --- UTILITY COMPONENT FACTORIES ---
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MUTED);
        return label;
    }

    // Existing getters and setter definitions maintained intact
    public void setGigDetails(int gigId, String title, double basePrice) {
        this.selectedGigId = gigId;
        this.basePriceAmount = basePrice;
        lblGigTitle.setText(title);
        lblBasePrice.setText(String.format("RM %.2f", basePrice));
        lblTotalEstimate.setText(String.format("RM %.2f", basePrice));
    }

    public int getSelectedGigId() { return selectedGigId; }
    public double getBasePriceAmount() { return basePriceAmount; }
    public int getQuantity() { return (int) spinQuantity.getValue(); }
    public boolean isExpressSelected() { return chkExpress.isSelected(); }
    public void setEstimatedTotal(double total) { lblTotalEstimate.setText(String.format("RM %.2f", total)); }

    public void addCalculateListener(ActionListener l) { btnCalculate.addActionListener(l); }
    public void addConfirmBookingListener(ActionListener l) { btnConfirmBooking.addActionListener(l); }
}