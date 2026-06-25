package controller;

import model.dao.BookingDAO;
import view.BuyerLogPanel;
import view.ReviewDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class BuyerLogController {
	private BuyerLogPanel view;
	private BookingDAO bookingDAO;
	private int buyerId;

	public BuyerLogController(BuyerLogPanel view, BookingDAO bookingDAO, int buyerId) {
		this.view = view;
		this.bookingDAO = bookingDAO;
		this.buyerId = buyerId;

		this.view.addReviewListener(new BuyerReviewListener());
		refreshLogTable();
		this.view.addTableMouseListener(new LogTableDoubleClickListener());
	}

	public void refreshLogTable() {

		DefaultTableModel activeModel = view.getActiveTableModel();
		DefaultTableModel historyModel = view.getHistoryTableModel();

		activeModel.setRowCount(0);
		historyModel.setRowCount(0);

		ArrayList<Vector<Object>> activeData = bookingDAO.getActiveBookings(buyerId);
		for (Vector<Object> row : activeData) {
			activeModel.addRow(row);
		}

		ArrayList<Vector<Object>> historyData = bookingDAO.getRecentHistoryBookings(buyerId);
		for (Vector<Object> row : historyData) {
			historyModel.addRow(row);
		}
	}

	private class BuyerReviewListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int bookingId = view.getSelectedBookingId();
			double price = view.getSelectedPrice();
			String status = view.getSelectedStatus();

			if (bookingId == -1) {
				JOptionPane.showMessageDialog(view, "Please highlight a booking log row from your tables first.",
						"Selection Required", JOptionPane.WARNING_MESSAGE);
				return;
			}

			if ("COMPLETED".equalsIgnoreCase(status)) {
				JOptionPane.showMessageDialog(view,
						"This order has already been finalized and paid for. You cannot review it again.",
						"Action Blocked", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!"READY_FOR_REVIEW".equalsIgnoreCase(status)) {
				JOptionPane.showMessageDialog(view,
						"You can only release payments for orders marked as 'READY_FOR_REVIEW' by the seller.",
						"Waiting for Seller Delivery", JOptionPane.WARNING_MESSAGE);
				return;
			}

			JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
			ReviewDialog reviewBox = new ReviewDialog(parentFrame, bookingId, price);

			reviewBox.addSubmitListener(evt -> {
				int selectedStars = reviewBox.getSelectedStars();

				double calculatedBase = price * 0.10;
				int pointsToAward = (int) Math.round(calculatedBase);
				if (selectedStars == 5) {
					pointsToAward = (int) Math.round(calculatedBase * 1.5);
				}
				if (pointsToAward < 1)
					pointsToAward = 1;

				int targetedSellerId = bookingDAO.getSellerIdFromBooking(bookingId);

				boolean transactionSuccess = bookingDAO.completeJobAndAwardPoints(bookingId, buyerId, targetedSellerId,
						price, pointsToAward);

				if (transactionSuccess) {

					HashMap<String, String> meta = bookingDAO.getBookingDetailsForEmailBySql(bookingId);

					if (!meta.isEmpty()) {
						String sellerEmail = meta.get("seller_email");
						String taskTitle = meta.get("task_title");
						String buyerName = meta.get("buyer_name");
						String sellerName = meta.get("seller_name");
						String quantity = meta.get("qty");
						String cost = meta.get("total_cost");

						String starDisplay = "★".repeat(selectedStars) + "☆".repeat(5 - selectedStars);

						if (sellerEmail != null) {

							String paymentHtmlBody = "<h2>💰 Virtual Funds Released & Review Received!</h2>"
									+ "<p>Hello <b>" + sellerName + "</b>,</p>" + "<p>Excellent work! The client (<b>"
									+ buyerName
									+ "</b>) has approved your delivery, accepted the final artifacts, and released the project funds to your account balance.</p>"
									+ "<hr style='border: 1px solid #eee;'/>"
									+ "<h3>💸 Transaction Invoicing Summary:</h3>" + "<ul>"
									+ "  <li><b>Project Identity:</b> " + taskTitle + " (Order #" + bookingId + ")</li>"
									+ "  <li><b>Quantity Units:</b> " + quantity + "</li>"
									+ "  <li><b>Total Transferred Payout:</b> RM " + cost
									+ " (Credited into Wallet)</li>" + "</ul>" + "<hr style='border: 1px solid #eee;'/>"
									+ "<h3>⭐ Client Evaluation & Gamification:</h3>" + "<ul>"
									+ "  <li><b>Performance Score Given:</b> <span style='color: #f39c12; font-size: 1.1em; font-weight: bold;'>"
									+ starDisplay + "</span> (" + selectedStars + "/5 Stars)</li>"
									+ "  <li><b>Experience Progression Earned:</b> <span style='color: #2ecc71; font-weight: bold;'>+"
									+ pointsToAward + " Service Points</span></li>" + "</ul>"
									+ "<hr style='border: 1px solid #eee;'/>"
									+ "<p>Your profile points tier standing has adjusted. Log back into your UniVerse Application Dashboard to monitor your earnings statement metrics.</p>";

							config.EmailUtility.sendNotification(sellerEmail, "💰 Payout Released: " + taskTitle,
									paymentHtmlBody);
						}
					}

					JOptionPane.showMessageDialog(reviewBox,
							"Payment released successfully! Transaction history updated.", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					reviewBox.dispose();
					refreshLogTable();
				} else {
					JOptionPane.showMessageDialog(reviewBox, "Transaction Declined! Insufficient virtual wallet funds.",
							"Funds Error", JOptionPane.ERROR_MESSAGE);
				}
			});

			reviewBox.setVisible(true);
		}
	}

	private class LogTableDoubleClickListener extends java.awt.event.MouseAdapter {
		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {

			if (e.getClickCount() == 2) {
				int selectedBookingId = view.getSelectedBookingId();

				if (selectedBookingId != -1) {

					java.util.HashMap<String, Object> detailedManifest = bookingDAO
							.getBookingDetailedManifest(selectedBookingId);

					if (!detailedManifest.isEmpty()) {
						JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);

						view.BookingDetailsDialog detailModal = new view.BookingDetailsDialog(parentFrame,
								detailedManifest);
						detailModal.setVisible(true);
					}
				}
			}
		}
	}
}