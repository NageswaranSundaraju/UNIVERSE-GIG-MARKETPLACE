package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class SellerActiveJobsPanel extends JPanel {
    private JTable tblJobs;
    private DefaultTableModel tableModel;
    private JButton btnComplete;

    public SellerActiveJobsPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Operational Workspace — Ongoing Active Assignments");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Order ID", "Service Title", "Client Name", "Quantity", "Project Value", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblJobs = new JTable(tableModel);
        add(new JScrollPane(tblJobs), BorderLayout.CENTER);

        // --- LOWER ACTION TOOLBAR ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnComplete = new JButton("Mark Project as Completed");
        btnComplete.setBackground(new Color(52, 152, 219)); // Clean operational blue accent
        btnComplete.setForeground(Color.WHITE);
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        actionPanel.add(btnComplete);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public DefaultTableModel getTableModel() { return tableModel; }

    public int getSelectedBookingId() {
        int selectedRow = tblJobs.getSelectedRow();
        if (selectedRow != -1) {
            return (int) tableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }

    public void addCompleteJobListener(ActionListener l) { btnComplete.addActionListener(l); }
}