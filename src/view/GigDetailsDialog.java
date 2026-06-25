package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

public class GigDetailsDialog extends JDialog {
    
    public GigDetailsDialog(JFrame parent, HashMap<String, Object> data) {
        super(parent, "Service Blueprint Specification", true);
        // 📏 Bumped height slightly to 550 to prevent layout compression
        setSize(500, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        Color colorBg = new Color(24, 28, 36);
        Color colorCard = new Color(17, 19, 24);
        Color colorTextLight = new Color(245, 246, 250);
        Color colorTextMuted = new Color(140, 147, 171);
        Color colorAccent = new Color(81, 107, 240);
        Color colorBorder = new Color(45, 52, 71);
        Color colorTierGold = new Color(241, 196, 15); 

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(colorBg);
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblHeader = new JLabel("🛒 Service Marketplace Listing Details");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setForeground(colorTextLight);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblHeader);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel cardBody = new JPanel();
        cardBody.setLayout(new GridLayout(0, 1, 0, 12));
        cardBody.setBackground(colorCard);
        cardBody.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorBorder, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        cardBody.setAlignmentX(Component.LEFT_ALIGNMENT);

        addMetaRow(cardBody, "GIG ID MATRIX:", "#" + data.getOrDefault("gig_id", "N/A"), colorTextMuted, colorTextLight);
        addMetaRow(cardBody, "SERVICE TITLE:", String.valueOf(data.getOrDefault("title", "N/A")), colorTextMuted, colorAccent);
        addMetaRow(cardBody, "OFFICIAL CATEGORY:", String.valueOf(data.getOrDefault("category", "N/A")), colorTextMuted, colorTextLight);
        addMetaRow(cardBody, "BASE UNIT PRICE:", String.format("RM %.2f", data.getOrDefault("base_price", 0.0)), colorTextMuted, new Color(46, 204, 113));
        
        cardBody.add(new JSeparator(JSeparator.HORIZONTAL));
        
        addMetaRow(cardBody, "LISTING SERVICE PROVIDER:", String.valueOf(data.getOrDefault("seller_name", "N/A")), colorTextMuted, colorTextLight);
        addMetaRow(cardBody, "SELLER CORRESPONDENCE:", String.valueOf(data.getOrDefault("seller_email", "N/A")), colorTextMuted, colorTextLight);
        
        // 👑 NEW ROW: Seller Ranking Tier rendering element
        addMetaRow(cardBody, "SELLER PERFORMANCE TIER:", String.valueOf(data.getOrDefault("tier_name", "BRONZE")).toUpperCase(), colorTextMuted, colorTierGold);

        mainPanel.add(cardBody);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblDescTitle = new JLabel("COMPREHENSIVE GIG PROFILE DESCRIPTION:");
        lblDescTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblDescTitle.setForeground(colorTextMuted);
        lblDescTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblDescTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextArea txtDesc = new JTextArea(String.valueOf(data.getOrDefault("description", "No description provided by service contractor.")));
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDesc.setForeground(colorTextLight);
        txtDesc.setBackground(colorCard);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorBorder, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setPreferredSize(new Dimension(440, 100));
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(scrollDesc);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnClose = new JButton("Return to Marketplace Panel");
        btnClose.setMaximumSize(new Dimension(450, 38));
        btnClose.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnClose.setBackground(colorBorder);
        btnClose.setForeground(colorTextLight);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());
        mainPanel.add(btnClose);

        add(mainPanel);
    }

    private void addMetaRow(JPanel container, String title, String val, Color tColor, Color vColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblT.setForeground(tColor);
        
        JLabel lblV = new JLabel(val);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblV.setForeground(vColor);
        
        row.add(lblT, BorderLayout.WEST);
        row.add(lblV, BorderLayout.EAST);
        container.add(row);
    }
}