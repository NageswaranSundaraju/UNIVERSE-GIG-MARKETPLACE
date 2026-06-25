package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class AdminDashboard extends JFrame {
    private final Color COLOR_BG = new Color(24, 28, 36);
    private final Color COLOR_CARD = new Color(32, 38, 50);
    private final Color COLOR_TEXT = new Color(245, 246, 250);
    private final Color COLOR_DANGER = new Color(231, 76, 60);
    private final Color COLOR_ACCENT = new Color(81, 107, 240);

    private DefaultTableModel userModel, gigModel;
    private JTable userTable, gigTable;

    private model.dao.UserDAO userDAO = new model.dao.UserDAO();
    private model.dao.GigDAO gigDAO = new model.dao.GigDAO();

    public AdminDashboard() {
        setTitle("UniVerse Workspace — [SYSTEM ADMINISTRATOR]");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_BG);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(COLOR_BG);
        tabbedPane.setForeground(COLOR_TEXT);
        tabbedPane.setFocusable(false);

        tabbedPane.addTab("👥 Identity Management", createUserPanel());
        tabbedPane.addTab("🛒 Task Enforcements", createGigPanel());

        add(tabbedPane, BorderLayout.CENTER);
        
        // Logout Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomBar.setBackground(COLOR_BG);
        JButton btnLogout = new JButton("Terminate Session");
        btnLogout.setBackground(COLOR_CARD);
        btnLogout.setForeground(COLOR_TEXT);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> {
            dispose();
            
             new view.LoginWindow().setVisible(true); 
        });
        bottomBar.add(btnLogout);
        add(bottomBar, BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"User ID", "Username", "Email", "Role", "Status"};
        userModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        userTable = new JTable(userModel);
        userTable.setBackground(COLOR_CARD);
        userTable.setForeground(COLOR_TEXT);
        userTable.getTableHeader().setBackground(COLOR_BG);
        userTable.getTableHeader().setForeground(COLOR_ACCENT);

        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        
        JButton btnToggle = new JButton("Toggle Active/Suspended State");
        btnToggle.setBackground(COLOR_ACCENT);
        btnToggle.setForeground(COLOR_TEXT);
        btnToggle.setFocusPainted(false);
        btnToggle.addActionListener(e -> toggleUser());
        controls.add(btnToggle);

        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createGigPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"Gig ID", "Task Title", "Category", "Provider"};
        gigModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        gigTable = new JTable(gigModel);
        gigTable.setBackground(COLOR_CARD);
        gigTable.setForeground(COLOR_TEXT);
        gigTable.getTableHeader().setBackground(COLOR_BG);
        gigTable.getTableHeader().setForeground(COLOR_ACCENT);

        panel.add(new JScrollPane(gigTable), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        
        JButton btnDelete = new JButton("Force Delete Task");
        btnDelete.setBackground(COLOR_DANGER);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(e -> deleteGig());
        controls.add(btnDelete);

        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshData() {
        userModel.setRowCount(0);
        gigModel.setRowCount(0);

        for (Vector<Object> row : userDAO.getAllUsersForAdmin()) userModel.addRow(row);
        for (Vector<Object> row : gigDAO.getAllGigsForAdmin()) gigModel.addRow(row);
    }

    private void toggleUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) return;
        
        int userId = (int) userModel.getValueAt(row, 0);
        String currentStatus = (String) userModel.getValueAt(row, 4);
        boolean makeActive = currentStatus.equals("SUSPENDED");

        if (userDAO.toggleUserStatus(userId, makeActive)) {
            JOptionPane.showMessageDialog(this, "Identity state updated successfully.");
            refreshData();
        }
    }

    private void deleteGig() {
        int row = gigTable.getSelectedRow();
        if (row == -1) return;

        int gigId = (int) gigModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete this task? This action cannot be reversed.", "Confirm Wipe", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (gigDAO.deleteGigAdmin(gigId)) {
                JOptionPane.showMessageDialog(this, "Task removed from the global marketplace.");
                refreshData();
            }
        }
    }
}