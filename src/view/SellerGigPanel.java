package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class SellerGigPanel extends JPanel {
    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JTextField txtPrice;
    private JComboBox<String> cmbCategory;
    private JButton btnCreateGig;
    
    private JTable tblMyGigs;
    private DefaultTableModel tableModel;
    private JButton btnDeleteGig;

    public SellerGigPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- LEFT PANEL: CREATE GIG FORM ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Host a New Campus Service"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Service Title:"), gbc);
        txtTitle = new JTextField(15);
        gbc.gridx = 1; formPanel.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Category:"), gbc);
        cmbCategory = new JComboBox<>(new String[]{"Academic", "Creative", "Technical", "Assistance"});
        gbc.gridx = 1; formPanel.add(cmbCategory, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Base Price (RM):"), gbc);
        txtPrice = new JTextField(15);
        gbc.gridx = 1; formPanel.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Description:"), gbc);
        txtDescription = new JTextArea(4, 15);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        gbc.gridx = 1; formPanel.add(descScroll, gbc);

        btnCreateGig = new JButton("Publish Service");
        btnCreateGig.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 6, 6, 6);
        formPanel.add(btnCreateGig, gbc);

        add(formPanel, BorderLayout.WEST);

        // --- CENTER PANEL: CURRENT LISTINGS GRID ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("My Active Marketplace Services"));

        String[] columns = {"Gig ID", "Service Title", "Category", "Price (RM)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblMyGigs = new JTable(tableModel);
        rightPanel.add(new JScrollPane(tblMyGigs), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDeleteGig = new JButton("Delete / Hide Service");
        btnDeleteGig.setBackground(new Color(180, 50, 50)); // Clear red action indicator
        actionPanel.add(btnDeleteGig);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
    }

    // Input Getters
    public String getGigTitle() { return txtTitle.getText().trim(); }
    public String getGigCategory() { return cmbCategory.getSelectedItem().toString(); }
    public String getGigPrice() { return txtPrice.getText().trim(); }
    public String getGigDescription() { return txtDescription.getText().trim(); }
    
    public int getSelectedGigId() {
        int row = tblMyGigs.getSelectedRow();
        return (row != -1) ? (int) tableModel.getValueAt(row, 0) : -1;
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    
    public void clearForm() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        cmbCategory.setSelectedIndex(0);
    }

    // Event Triggers
    public void addCreateGigListener(ActionListener listener) { btnCreateGig.addActionListener(listener); }
    public void addDeleteGigListener(ActionListener listener) { btnDeleteGig.addActionListener(listener); }
}