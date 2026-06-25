package controller;

import model.dao.BookingDAO;
import view.SellerRequestsPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class SellerRequestsController {
    private SellerRequestsPanel view;
    private BookingDAO bookingDAO;
    private int sellerId;

    public SellerRequestsController(SellerRequestsPanel view, BookingDAO bookingDAO, int sellerId) {
        this.view = view;
        this.bookingDAO = bookingDAO;
        this.sellerId = sellerId;

        this.view.addAcceptListener(new AcceptJobListener());
        this.view.addDeclineListener(new DeclineJobListener());

        refreshRequestsTable();
    }

    public void refreshRequestsTable() {
        DefaultTableModel model = view.getTableModel();
        model.setRowCount(0);

        ArrayList<Vector<Object>> data = bookingDAO.getSellerIncomingRequests(sellerId);
        for (Vector<Object> row : data) {
            model.addRow(row);
        }
    }

    private class AcceptJobListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int bookingId = view.getSelectedBookingId();
            if (bookingId == -1) {
                JOptionPane.showMessageDialog(view, "Please select an incoming job row to accept.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }

       
            HashMap<String, String> meta = bookingDAO.getBookingDetailsForEmailBySql(bookingId);

            if (bookingDAO.updateBookingStatus(bookingId, "IN_PROGRESS")) {
                if (!meta.isEmpty()) {
                    String buyerEmail = meta.get("buyer_email");
                    String taskTitle = meta.get("task_title");
                    String buyerName = meta.get("buyer_name");
                    String sellerName = meta.get("seller_name");
                    String quantity = meta.get("qty");
                    String trackingSpeed = meta.get("express_flag");
                    String cost = meta.get("total_cost");

                    if (buyerEmail != null) {
                        String emailHtmlBody = 
                            "<h2>✅ Your Order Has Been Accepted!</h2>" +
                            "<p>Hello <b>" + buyerName + "</b>,</p>" +
                            "<p>Great news! The service provider (<b>" + sellerName + "</b>) has approved your incoming request and is working on it.</p>" +
                            "<hr style='border: 1px solid #eee;'/>" +
                            "<h3>📋 Order Invoice Blueprint:</h3>" +
                            "<ul>" +
                            "  <li><b>Selected Task:</b> " + taskTitle + " (Order #" + bookingId + ")</li>" +
                            "  <li><b>Quantity Requested:</b> " + quantity + " Unit(s)</li>" +
                            "  <li><b>Processing Priority:</b> " + trackingSpeed + "</li>" +
                            "  <li><b>Total Transacted Amount:</b> RM " + cost + "</li>" +
                            "</ul>" +
                            "<hr style='border: 1px solid #eee;'/>" +
                            "<p>You can track the ongoing generation progress directly within your <b>My Bookings Log</b> platform view.</p>";

                        config.EmailUtility.sendNotification(buyerEmail, "✅ Order Confirmed: " + taskTitle, emailHtmlBody);
                    }
                }
                
                JOptionPane.showMessageDialog(view, "Order Accepted! Moved to operational dashboard workflow.", "Job Confirmed", JOptionPane.INFORMATION_MESSAGE);
                refreshRequestsTable();
            }
        }
    }

    private class DeclineJobListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int bookingId = view.getSelectedBookingId();
            if (bookingId == -1) {
                JOptionPane.showMessageDialog(view, "Please select a job row to decline.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to decline this project request?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                
                HashMap<String, String> meta = bookingDAO.getBookingDetailsForEmailBySql(bookingId);

                if (bookingDAO.updateBookingStatus(bookingId, "CANCELLED")) {
                    if (!meta.isEmpty()) {
                        String buyerEmail = meta.get("buyer_email");
                        String taskTitle = meta.get("task_title");
                        String buyerName = meta.get("buyer_name");
                        String cost = meta.get("total_cost");

                        if (buyerEmail != null) {
                            String declineHtmlBody = 
                                "<h2>🛑 Notice: Booking Request Declined</h2>" +
                                "<p>Hello <b>" + buyerName + "</b>,</p>" +
                                "<p>We regret to inform you that your booking request has been cancelled by the service provider.</p>" +
                                "<hr style='border: 1px solid #eee;'/>" +
                                "<h3>📦 Reference Manifest Details:</h3>" +
                                "<ul>" +
                                "  <li><b>Task Name:</b> " + taskTitle + "</li>" +
                                "  <li><b>Order Number:</b> #" + bookingId + "</li>" +
                                "  <li><b>Refunded Amount:</b> RM " + cost + "</li>" +
                                "</ul>" +
                                "<hr style='border: 1px solid #eee;'/>" +
                                "<p>Any virtual funds allocated for this transaction lock have been successfully restored to your platform wallet balance.</p>";

                            config.EmailUtility.sendNotification(buyerEmail, "❌ Order Declined: " + taskTitle, declineHtmlBody);
                        }
                    }

                    JOptionPane.showMessageDialog(view, "Order cancelled. Client notified.");
                    refreshRequestsTable();
                }
            }
        }
    }
}