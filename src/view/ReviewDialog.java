package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReviewDialog extends JDialog {
    private JComboBox<Integer> comboStars;
    private JButton btnSubmit;
    private int bookingId;
    private double originalPrice;

    public ReviewDialog(JFrame parent, int bookingId, double originalPrice) {
        super(parent, "Rate Completed Assignment", true);
        this.bookingId = bookingId;
        this.originalPrice = originalPrice;
        
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Select Star Rating for Service Quality:"));
        
        // 1 to 5 Star Rating Options
        comboStars = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        comboStars.setSelectedIndex(4); // Default select 5 Stars
        mainPanel.add(comboStars);

        add(mainPanel, BorderLayout.CENTER);

        btnSubmit = new JButton("Submit Rating & Release Funds");
        add(btnSubmit, BorderLayout.SOUTH);
    }

    public int getSelectedStars() { return (int) comboStars.getSelectedItem(); }
    public int getBookingId() { return bookingId; }
    public double getOriginalPrice() { return originalPrice; }
    
    public void addSubmitListener(ActionListener l) { btnSubmit.addActionListener(l); }
}