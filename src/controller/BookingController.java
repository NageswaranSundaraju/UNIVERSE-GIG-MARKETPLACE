package controller;

import model.dao.BookingDAO;
import view.CheckoutWindow;
import view.BuyerLogPanel;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class BookingController {
	private CheckoutWindow view;
	private BookingDAO bookingDAO;
	private int currentBuyerId;
	private BuyerLogPanel logPanel;

	public BookingController(CheckoutWindow view, BookingDAO bookingDAO, int buyerId, BuyerLogPanel logPanel) {
		this.view = view;
		this.bookingDAO = bookingDAO;
		this.currentBuyerId = buyerId;
		this.logPanel = logPanel;

		this.view.addCalculateListener(new CalculatePriceListener());
		this.view.addConfirmBookingListener(new ConfirmBookingListener());
	}

	private double calculateFinalPrice() {
		return (view.getBasePriceAmount() * view.getQuantity()) * (view.isExpressSelected() ? 1.30 : 1.0);
	}

	private class CalculatePriceListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			view.setEstimatedTotal(calculateFinalPrice());
		}
	}

	private class ConfirmBookingListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			double finalPrice = calculateFinalPrice();
			int choice = JOptionPane.showConfirmDialog(view,
					"Confirm order for RM " + String.format("%.2f", finalPrice) + "?", "Place Order",
					JOptionPane.YES_NO_OPTION);

			if (choice == JOptionPane.YES_OPTION) {
				boolean success = bookingDAO.createBooking(currentBuyerId, view.getSelectedGigId(), view.getQuantity(),
						view.isExpressSelected(), finalPrice);

				if (success) {

					try {
						model.dao.NotificationDAO notifyDAO = new model.dao.NotificationDAO();

						HashMap<String, String> emailMeta = bookingDAO
								.getOrderNotificationMetadataBySql(view.getSelectedGigId(), currentBuyerId);

						if (!emailMeta.isEmpty()) {
							String sellerEmail = emailMeta.get("seller_email");
							String serviceName = emailMeta.get("task_title");
							String buyerName = emailMeta.get("buyer_name");
							String buyerEmail = emailMeta.get("buyer_email");
							String shippingSpeed = view.isExpressSelected() ? "Express Delivery (30% Surcharge Applied)"
									: "Standard Delivery";

							if (sellerEmail != null) {
								String emailHtmlBody = "<h2>⚡ You Have Received a New Job Request!</h2>"
										+ "<p>A buyer has purchased one of your listed services. Below are the order details for your reference:</p>"
										+ "<hr style='border: 1px solid #eee;'/>"
										+ "<h3>📦 Manifest Order Details:</h3>" + "<ul>"
										+ "  <li><b>Service Selected:</b> " + serviceName + "</li>"
										+ "  <li><b>Quantity Ordered:</b> " + view.getQuantity() + " Units</li>"
										+ "  <li><b>Delivery Priority:</b> " + shippingSpeed + "</li>"
										+ "  <li><b>Total Projected Payout:</b> RM " + String.format("%.2f", finalPrice)
										+ "</li>" + "</ul>" + "<hr style='border: 1px solid #eee;'/>"
										+ "<h3>👥 Client Profile Details:</h3>" + "<ul>"
										+ "  <li><b>Buyer Username:</b> " + buyerName + "</li>"
										+ "  <li><b>Contact Correspondence:</b> " + buyerEmail + "</li>" + "</ul>"
										+ "<hr style='border: 1px solid #eee;'/>"
										+ "<p>Please log into your UniVerse application workspace and visit the <b>Incoming Requests</b> panel to accept and begin this assignment.</p>";

								config.EmailUtility.sendNotification(sellerEmail,
										"🔔 New Order Request: " + serviceName, emailHtmlBody);
							}

							int targetSellerId = getSellerIdFromGig(view.getSelectedGigId());
							if (targetSellerId != -1) {
								notifyDAO.pushNotification(targetSellerId,
										"🔔 New Order: " + view.getQuantity() + "x " + serviceName + " purchased by "
												+ buyerName + " (RM " + String.format("%.2f", finalPrice) + ").");
							}
						}
					} catch (Exception ex) {
						System.err.println(
								"CRITICAL: Error compiling dynamic SQL notification manifest context details.");
						ex.printStackTrace();
					}

					if (logPanel != null) {
						logPanel.getActiveTableModel().setRowCount(0);
						logPanel.getHistoryTableModel().setRowCount(0);

						ArrayList<Vector<Object>> activeJobs = bookingDAO.getActiveBookings(currentBuyerId);
						ArrayList<Vector<Object>> pastHistory = bookingDAO.getRecentHistoryBookings(currentBuyerId);

						for (Vector<Object> row : activeJobs) {
							logPanel.getActiveTableModel().addRow(row);
						}
						for (Vector<Object> row : pastHistory) {
							logPanel.getHistoryTableModel().addRow(row);
						}
					}

					JOptionPane.showMessageDialog(view, "Order submitted successfully!");
					view.dispose();
				} else {
					JOptionPane.showMessageDialog(view, "Transaction processing failure.", "Database Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	private int getSellerIdFromGig(int gigId) {
		String sql = "SELECT seller_id FROM gigs WHERE gig_id = ?";
		try (java.sql.Connection conn = config.DatabaseConnection.getConnection();
				java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, gigId);
			try (java.sql.ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("seller_id");
				}
			}
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
}