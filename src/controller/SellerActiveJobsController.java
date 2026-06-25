package controller;

import model.dao.BookingDAO;
import view.SellerActiveJobsPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class SellerActiveJobsController {
	private SellerActiveJobsPanel view;
	private BookingDAO bookingDAO;
	private int sellerId;

	public SellerActiveJobsController(SellerActiveJobsPanel view, BookingDAO bookingDAO, int sellerId) {
		this.view = view;
		this.bookingDAO = bookingDAO;
		this.sellerId = sellerId;

		this.view.addCompleteJobListener(new CompleteJobListener());
		refreshActiveJobsTable();
	}

	public void refreshActiveJobsTable() {
		DefaultTableModel model = view.getTableModel();
		model.setRowCount(0);

		ArrayList<Vector<Object>> data = bookingDAO.getSellerActiveJobs(sellerId);
		for (Vector<Object> row : data) {
			model.addRow(row);
		}
	}

	private class CompleteJobListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int bookingId = view.getSelectedBookingId();
			if (bookingId == -1) {
				JOptionPane.showMessageDialog(view, "Please highlight an active job row to mark complete.",
						"Selection Missing", JOptionPane.WARNING_MESSAGE);
				return;
			}

			int choice = JOptionPane.showConfirmDialog(view,
					"Confirm completion? This will send the project to the buyer for approval and payment release.",
					"Deliver Project", JOptionPane.YES_NO_OPTION);

			if (choice == JOptionPane.YES_OPTION) {

				HashMap<String, String> meta = bookingDAO.getBookingDetailsForEmailBySql(bookingId);

				if (bookingDAO.updateBookingStatus(bookingId, "READY_FOR_REVIEW")) {

					if (!meta.isEmpty()) {
						String buyerEmail = meta.get("buyer_email");
						String taskTitle = meta.get("task_title");
						String buyerName = meta.get("buyer_name");
						String sellerName = meta.get("seller_name");
						String quantity = meta.get("qty");
						String trackingSpeed = meta.get("express_flag");
						String cost = meta.get("total_cost");

						if (buyerEmail != null) {

							String deliveryHtmlBody = "<h2>📦 Your Service Has Been Delivered & Ready for Review!</h2>"
									+ "<p>Hello <b>" + buyerName + "</b>,</p>" + "<p>Your project manager <b>"
									+ sellerName
									+ "</b> has marked your active order as finished and uploaded the delivery artifacts.</p>"
									+ "<hr style='border: 1px solid #eee;'/>"
									+ "<h3>📋 Completed Project Manifest:</h3>" + "<ul>"
									+ "  <li><b>Delivered Service:</b> " + taskTitle + " (Order #" + bookingId
									+ ")</li>" + "  <li><b>Quantity Completed:</b> " + quantity + " Unit(s)</li>"
									+ "  <li><b>Delivery Velocity Tier:</b> " + trackingSpeed + "</li>"
									+ "  <li><b>Total Transacted Cost:</b> RM " + cost + "</li>" + "</ul>"
									+ "<hr style='border: 1px solid #eee;'/>"
									+ "<p><b>Next Steps:</b> Please review the files, head over to your <b>My Bookings Log</b> dashboard panel within your UniVerse app workspace, verify the work quality, and select <b>Release Funds</b> to finalize the payment loop.</p>";

							config.EmailUtility.sendNotification(buyerEmail, "📦 Delivery Confirmation: " + taskTitle,
									deliveryHtmlBody);
						}
					}

					JOptionPane.showMessageDialog(view,
							"Project status updated! Waiting for buyer verification and release.",
							"Delivery Dispatched", JOptionPane.INFORMATION_MESSAGE);
					refreshActiveJobsTable();
				}
			}
		}
	}
}