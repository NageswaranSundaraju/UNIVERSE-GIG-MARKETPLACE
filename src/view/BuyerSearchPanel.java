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

public class BuyerSearchPanel extends JPanel {
    private JTextField txtSearch;
    private JComboBox<String> cmbCategory;
    private JButton btnSearch;
    private JTable tblGigs;
    private DefaultTableModel tableModel;
    private JButton btnBookOrder;

    // Styled Palette Design Matrix (Matches MainDashboard and LoginWindow flawlessly)
    private final Color COLOR_BG           = new Color(24, 28, 36);   // Dark Slate Base
    private final Color COLOR_CARD_BG      = new Color(17, 19, 24);   // Deep Onyx Bar Container
    private final Color COLOR_ACCENT       = new Color(81, 107, 240);  // Indigo Premium Brand Highlight
    private final Color COLOR_INPUT_BG     = new Color(32, 38, 50);   // Dark Input Field Canvas
    private final Color COLOR_TEXT_LIGHT   = new Color(245, 246, 250); // Crisp Off-White Core Text
    private final Color COLOR_TEXT_MUTED   = new Color(140, 147, 171); // Soft Slate Gray Form Labels
    private final Color COLOR_BORDER       = new Color(45, 52, 71);    // Subtle input divider line

    public BuyerSearchPanel() {
        // Apply background structure
        setBackground(COLOR_BG);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Elegant outer panel spacing gap

        // --- TOP FILTER BAR ---
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(COLOR_CARD_BG);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Custom Styled Text Input Field
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(0, 38));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBackground(COLOR_INPUT_BG);
        txtSearch.setForeground(COLOR_TEXT_LIGHT);
        txtSearch.setCaretColor(COLOR_TEXT_LIGHT);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            new EmptyBorder(0, 12, 0, 12)
        ));
        txtSearch.putClientProperty("JTextField.placeholderText", "Search campus services (e.g., Proofreading)...");

        // Custom Styled Dropdown Choice Selector
        cmbCategory = new JComboBox<>(new String[]{"All Categories", "Academic", "Creative", "Technical", "Assistance"});
        cmbCategory.setPreferredSize(new Dimension(150, 38));
        cmbCategory.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbCategory.setBackground(COLOR_INPUT_BG);
        cmbCategory.setForeground(COLOR_TEXT_LIGHT);
        cmbCategory.setFocusable(false);
        cmbCategory.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        // Inline Secondary Search Trigger Button
        btnSearch = new JButton("Search");
        btnSearch.setPreferredSize(new Dimension(100, 38));
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setForeground(COLOR_TEXT_LIGHT);
        btnSearch.setBackground(new Color(45, 52, 71));
        btnSearch.setFocusPainted(false);
        btnSearch.setBorderPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnSearch.setBackground(new Color(58, 67, 92)); }
            @Override
            public void mouseExited(MouseEvent e) { btnSearch.setBackground(new Color(45, 52, 71)); }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        filterPanel.add(txtSearch, gbc);
        gbc.gridx = 1; gbc.weightx = 0.0;
        filterPanel.add(cmbCategory, gbc);
        gbc.gridx = 2;
        filterPanel.add(btnSearch, gbc);

        add(filterPanel, BorderLayout.NORTH);

        // --- CENTER DATA TABLE ---
        String[] columns = {"Gig ID", "Service Title", "Category", "Base Price (RM)", "Seller ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tblGigs = new JTable(tableModel);
        tblGigs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGigs.setRowHeight(32); // Increased row boundary space for clean layout breathing room
        tblGigs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblGigs.setBackground(COLOR_CARD_BG);
        tblGigs.setForeground(COLOR_TEXT_LIGHT);
        tblGigs.setGridColor(COLOR_BORDER);
        tblGigs.setShowVerticalLines(false); // Clean modern ledger aesthetic layout look
        tblGigs.setSelectionBackground(new Color(38, 45, 61));
        tblGigs.setSelectionForeground(COLOR_TEXT_LIGHT);

        // Style the Header row block of the table
        JTableHeader tableHeader = tblGigs.getTableHeader();
        tableHeader.setReorderingAllowed(false);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setBackground(COLOR_INPUT_BG);
        tableHeader.setForeground(COLOR_TEXT_MUTED);
        tableHeader.setPreferredSize(new Dimension(0, 36));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER));

        // Center align table contents cleanly
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(noFocusBorder); // Eliminate annoying inner dotted selector lines
                return c;
            }
        };
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        tblGigs.setDefaultRenderer(Object.class, cellRenderer);

        JScrollPane scrollPane = new JScrollPane(tblGigs);
        scrollPane.getViewport().setBackground(COLOR_BG); // Prevents default blinding white grid fallback empty spaces
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        add(scrollPane, BorderLayout.CENTER);

        // --- BOTTOM ACTION BAR ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setOpaque(false);
        
        btnBookOrder = new JButton("Book Selected Service");
        btnBookOrder.setPreferredSize(new Dimension(190, 42));
        btnBookOrder.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnBookOrder.setForeground(COLOR_TEXT_LIGHT);
        btnBookOrder.setBackground(COLOR_ACCENT);
        btnBookOrder.setFocusPainted(false);
        btnBookOrder.setBorderPainted(false);
        btnBookOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBookOrder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnBookOrder.setBackground(COLOR_ACCENT.brighter()); }
            @Override
            public void mouseExited(MouseEvent e) { btnBookOrder.setBackground(COLOR_ACCENT); }
        });
        actionPanel.add(btnBookOrder);

        add(actionPanel, BorderLayout.SOUTH);
    }

    // Interactive Control Accessors
    public String getSearchKeyword() { return txtSearch.getText().trim(); }
    public String getSelectedCategory() { return cmbCategory.getSelectedItem().toString(); }
    
    public int getSelectedGigId() {
        int selectedRow = tblGigs.getSelectedRow();
        if (selectedRow != -1) {
            return (int) tableModel.getValueAt(selectedRow, 0);
        }
        return -1;
    }
    
    public String getSelectedGigTitle() {
        int selectedRow = tblGigs.getSelectedRow();
        if (selectedRow != -1) {
            return tableModel.getValueAt(selectedRow, 1).toString();
        }
        return null;
    }

    public double getSelectedGigPrice() {
        int selectedRow = tblGigs.getSelectedRow();
        if (selectedRow != -1) {
            Object val = tableModel.getValueAt(selectedRow, 3);
            return Double.parseDouble(val.toString());
        }
        return 0.0;
    }

    public DefaultTableModel getTableModel() { return tableModel; }

    // Event Registration Handlers
    public void addSearchListener(ActionListener listener) { btnSearch.addActionListener(listener); }
    public void addBookOrderListener(ActionListener listener) { btnBookOrder.addActionListener(listener); }
    
    public void addTableMouseListener(java.awt.event.MouseAdapter adapter) {
        // Replace 'table' with the actual JTable variable name inside your BuyerSearchPanel file!
        this.tblGigs.addMouseListener(adapter); 
    }
}