package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BookingPanel extends JPanel {
    private JLabel lblGigTitle;
    private JLabel lblBasePrice;
    private JSpinner spinQuantity;
    private JCheckBox chkExpress;
    private JLabel lblTotalEstimate;
    private JButton btnCalculate;
    private JButton btnConfirmBooking;
    
    private int selectedGigId;
    private double basePriceAmount;

    public BookingPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

      
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Configure Your Service Booking"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Service Name Display
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Selected Service:"), gbc);
        lblGigTitle = new JLabel("None Selected");
        lblGigTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1; formPanel.add(lblGigTitle, gbc);

        // Base Price Display
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Base Rate:"), gbc);
        lblBasePrice = new JLabel("RM 0.00");
        gbc.gridx = 1; formPanel.add(lblBasePrice, gbc);

        // Quantity Modifier
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Quantity / Units:"), gbc);
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc.gridx = 1; formPanel.add(spinQuantity, gbc);

   
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Delivery Speed:"), gbc);
        chkExpress = new JCheckBox("Express Delivery (24 Hours) +30% Premium");
        gbc.gridx = 1; formPanel.add(chkExpress, gbc);

  
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Estimated Total:"), gbc);
        lblTotalEstimate = new JLabel("RM 0.00");
        lblTotalEstimate.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalEstimate.setForeground(new Color(46, 204, 113)); 
        gbc.gridx = 1; formPanel.add(lblTotalEstimate, gbc);

        add(formPanel, BorderLayout.CENTER);

  
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnCalculate = new JButton("Recalculate Price");
        btnConfirmBooking = new JButton("Confirm & Place Order");
        btnConfirmBooking.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        actionPanel.add(btnCalculate);
        actionPanel.add(btnConfirmBooking);
        add(actionPanel, BorderLayout.SOUTH);
    }

    // Setters to populate selected gig details dynamically from the search table
    public void setGigDetails(int gigId, String title, double basePrice) {
        this.selectedGigId = gigId;
        this.basePriceAmount = basePrice;
        lblGigTitle.setText(title);
        lblBasePrice.setText(String.format("RM %.2f", basePrice));
        setEstimatedTotal(basePrice); // Initial reset
    }

    // Getters and Setters for values
    public int getSelectedGigId() { return selectedGigId; }
    public double getBasePriceAmount() { return basePriceAmount; }
    public int getQuantity() { return (int) spinQuantity.getValue(); }
    public boolean isExpressSelected() { return chkExpress.isSelected(); }
    public void setEstimatedTotal(double total) { lblTotalEstimate.setText(String.format("RM %.2f", total)); }

    // Action Triggers
    public void addCalculateListener(ActionListener listener) { btnCalculate.addActionListener(listener); }
    public void addConfirmBookingListener(ActionListener listener) { btnConfirmBooking.addActionListener(listener); }
}