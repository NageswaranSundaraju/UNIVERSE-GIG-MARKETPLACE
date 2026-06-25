package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class SellerRequestsPanel extends JPanel {
    private JTable tblRequests;
    private DefaultTableModel tableModel;
    private JButton btnAccept;
    private JButton btnDecline;

    public SellerRequestsPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Incoming Service Orders & Client Work Pipelines");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Service Title", "Client Name", "Quantity Ordered", "Total Revenue", "Current Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblRequests = new JTable(tableModel);
        add(new JScrollPane(tblRequests), BorderLayout.CENTER);

        // --- LOWER WORKFLOW ACTION CONTROLS ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnAccept = new JButton("Accept Job & Start Work");
        btnAccept.setBackground(new Color(46, 204, 113));
        btnAccept.setForeground(Color.WHITE);
        
        btnDecline = new JButton("Decline / Cancel Order");
        
        actionPanel.add(btnDecline);
        actionPanel.add(btnAccept);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() { return tableModel; }

    public int getSelectedBookingId() {
        int selectedRow = tblRequests.getSelectedRow();
        if (selectedRow != -1) {
            return (int) tableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    public void addAcceptListener(ActionListener l) { btnAccept.addActionListener(l); }
    public void addDeclineListener(ActionListener l) { btnDecline.addActionListener(l); }
}