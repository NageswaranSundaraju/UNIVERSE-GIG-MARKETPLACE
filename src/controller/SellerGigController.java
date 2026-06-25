package controller;

import model.dao.GigDAO;
import view.SellerGigPanel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class SellerGigController {
	private SellerGigPanel view;
    private GigDAO gigDAO;
    private int currentSellerId;

    
    public SellerGigController(SellerGigPanel view, GigDAO gigDAO, int sellerId) {
        this.view = view;
        this.gigDAO = gigDAO;
        this.currentSellerId = sellerId;

        this.view.addCreateGigListener(new PublishServiceListener());
        this.view.addDeleteGigListener(new HideServiceListener());

        refreshSellerTable();
    }

    private void refreshSellerTable() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);
        
        ArrayList<Vector<Object>> data = gigDAO.getGigsBySeller(currentSellerId);
        for (Vector<Object> row : data) {
            model.addRow(row);
        }
    }

    private class PublishServiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = view.getGigTitle();
            String category = view.getGigCategory();
            String priceStr = view.getGigPrice();
            String desc = view.getGigDescription();

            if (title.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(view, "All fields are required to list a service.", "Form Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);
                boolean success = gigDAO.createGig(currentSellerId, title, category, price, desc);
                
                if (success) {
                    JOptionPane.showMessageDialog(view, "Service published live to campus marketplace!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    view.clearForm();
                    refreshSellerTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Please input a valid price decimal amount (e.g. 15.50).", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class HideServiceListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedGigId = view.getSelectedGigId();
            if (selectedGigId == -1) {
                
                JOptionPane.showMessageDialog(view, "Please select a service row from the table to drop.", "Selection Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to remove this listing from the marketplace?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (gigDAO.softDeleteGig(selectedGigId)) {
                    JOptionPane.showMessageDialog(view, "Listing hidden successfully.");
                    refreshSellerTable();
                }
            }
        }
    }
}