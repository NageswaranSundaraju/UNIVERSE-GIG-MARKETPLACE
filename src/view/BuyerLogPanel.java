package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BuyerLogPanel extends JPanel {
    private JTable tblActiveLogs;
    private JTable tblHistoryLogs;
    
    private DefaultTableModel activeModel;
    private DefaultTableModel historyModel;
    private JButton btnReviewJob;

    // Premium Theme Color Settings
    private final Color COLOR_BG           = new Color(24, 28, 36);   
    private final Color COLOR_CARD_BG      = new Color(17, 19, 24);   
    private final Color COLOR_ACCENT       = new Color(155, 89, 182); 
    private final Color COLOR_INPUT_BG     = new Color(32, 38, 50);   
    private final Color COLOR_TEXT_LIGHT   = new Color(245, 246, 250); 
    private final Color COLOR_TEXT_MUTED   = new Color(140, 147, 171); 
    private final Color COLOR_BORDER       = new Color(45, 52, 71);    

    public BuyerLogPanel() {
        setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 15));
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // Shared data column definitions
        String[] columns = {"Order ID", "Service Description", "Seller Name", "Amount Paid", "Progress Status"};

        // --- SPLIT GRID WORKSPACE CONTAINER ---
        JPanel tablesContainer = new JPanel(new GridLayout(2, 1, 0, 20));
        tablesContainer.setOpaque(false);

        // ==========================================
        // 1. TOP SECTION: ACTIVE BOOKINGS
        // ==========================================
        JPanel activePanel = new JPanel(new BorderLayout(0, 8));
        activePanel.setOpaque(false);
        
        JLabel lblActiveTitle = new JLabel("⚡ Active Service Bookings & Operational Progress");
        lblActiveTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblActiveTitle.setForeground(COLOR_TEXT_LIGHT);
        activePanel.add(lblActiveTitle, BorderLayout.NORTH);

        activeModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblActiveLogs = createStyledTable(activeModel);
        
        JScrollPane scrollActive = new JScrollPane(tblActiveLogs);
        scrollActive.getViewport().setBackground(COLOR_BG);
        scrollActive.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        activePanel.add(scrollActive, BorderLayout.CENTER);
        
        tablesContainer.add(activePanel);

        // ==========================================
        // 2. BOTTOM SECTION: RECENT COMPLETED HISTORY
        // ==========================================
        JPanel historyPanel = new JPanel(new BorderLayout(0, 8));
        historyPanel.setOpaque(false);
        
        JLabel lblHistoryTitle = new JLabel("📜 Recent History Log (Last 5 Completed Tasks)");
        lblHistoryTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHistoryTitle.setForeground(COLOR_TEXT_MUTED);
        historyPanel.add(lblHistoryTitle, BorderLayout.NORTH);

        historyModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistoryLogs = createStyledTable(historyModel);
        
        JScrollPane scrollHistory = new JScrollPane(tblHistoryLogs);
        scrollHistory.getViewport().setBackground(COLOR_BG);
        scrollHistory.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        historyPanel.add(scrollHistory, BorderLayout.CENTER);
        
        tablesContainer.add(historyPanel);

        add(tablesContainer, BorderLayout.CENTER);

        // --- BOTTOM ACTION TRIGGER TOOLBAR ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        actionPanel.setOpaque(false);
        
        btnReviewJob = new JButton("Confirm Delivery & Rate Seller");
        btnReviewJob.setPreferredSize(new Dimension(240, 40));
        btnReviewJob.setBackground(COLOR_ACCENT); 
        btnReviewJob.setForeground(COLOR_TEXT_LIGHT);
        btnReviewJob.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReviewJob.setFocusPainted(false);
        btnReviewJob.setBorderPainted(false);
        btnReviewJob.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnReviewJob.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnReviewJob.setBackground(COLOR_ACCENT.brighter()); }
            @Override public void mouseExited(MouseEvent e) { btnReviewJob.setBackground(COLOR_ACCENT); }
        });
        
        actionPanel.add(btnReviewJob);
        add(actionPanel, BorderLayout.SOUTH);
    }

    // --- REUSABLE CUSTOM UI COMPONENT FACTORY ---
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(COLOR_CARD_BG);
        table.setForeground(COLOR_TEXT_LIGHT);
        table.setGridColor(COLOR_BORDER);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(43, 35, 54));
        table.setSelectionForeground(COLOR_TEXT_LIGHT);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(COLOR_INPUT_BG);
        header.setForeground(COLOR_TEXT_MUTED);
        header.setPreferredSize(new Dimension(0, 34));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isSel, hasF, r, c);
                setBorder(noFocusBorder);
                return comp;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);
        return table;
    }

    // --- MODEL DATA ACCESSORS ---
    public DefaultTableModel getActiveTableModel() { return activeModel; }
    public DefaultTableModel getHistoryTableModel() { return historyModel; }

    /**
     * Contextually checks both table frameworks to find which row cell the buyer selected.
     */
    private JTable getCurrentlySelectedTable() {
        if (tblActiveLogs.getSelectedRow() != -1) return tblActiveLogs;
        if (tblHistoryLogs.getSelectedRow() != -1) return tblHistoryLogs;
        return null;
    }

    public int getSelectedBookingId() {
        JTable activeTable = getCurrentlySelectedTable();
        if (activeTable != null) {
            int row = activeTable.getSelectedRow();
            return (int) activeTable.getModel().getValueAt(row, 0);
        }
        return -1;
    }

    public double getSelectedPrice() {
        JTable activeTable = getCurrentlySelectedTable();
        if (activeTable != null) {
            int row = activeTable.getSelectedRow();
            return Double.parseDouble(activeTable.getModel().getValueAt(row, 3).toString());
        }
        return 0.0;
    }

    public String getSelectedStatus() {
        JTable activeTable = getCurrentlySelectedTable();
        if (activeTable != null) {
            int row = activeTable.getSelectedRow();
            return activeTable.getModel().getValueAt(row, 4).toString();
        }
        return "";
    }

    public void addReviewListener(ActionListener l) { btnReviewJob.addActionListener(l); }
    
    public void addTableMouseListener(java.awt.event.MouseAdapter adapter) {
        tblActiveLogs.addMouseListener(adapter);
        tblHistoryLogs.addMouseListener(adapter);
    }
}