package controller;

import model.dao.GigDAO;
import view.BuyerSearchPanel;
import view.MainDashboard;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

public class MarketplaceController {
	private BuyerSearchPanel view;
	private GigDAO gigDAO;
	private MainDashboard dashboard;

	public MarketplaceController(BuyerSearchPanel view, GigDAO gigDAO, MainDashboard dashboard) {
		this.view = view;
		this.gigDAO = gigDAO;
		this.dashboard = dashboard;

		this.view.addSearchListener(new SearchEngineListener());
		this.view.addBookOrderListener(new BookButtonListener());

		this.view.addTableMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selectedGigId = view.getSelectedGigId();
					if (selectedGigId != -1) {

						java.util.HashMap<String, Object> gigDetails = gigDAO.getGigDetailedManifest(selectedGigId);

						if (!gigDetails.isEmpty()) {
							javax.swing.JFrame parentFrame = (javax.swing.JFrame) javax.swing.SwingUtilities
									.getWindowAncestor(view);

							view.GigDetailsDialog detailsBox = new view.GigDetailsDialog(parentFrame, gigDetails);
							detailsBox.setVisible(true);
						}
					}
				}
			}
		});

		refreshTable("", "All Categories");
	}

	private void refreshTable(String keyword, String category) {
		DefaultTableModel model = view.getTableModel();
		model.setRowCount(0);

		ArrayList<Vector<Object>> data = gigDAO.searchGigs(keyword, category);
		for (Vector<Object> row : data) {
			model.addRow(row);
		}
	}

	private class SearchEngineListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String keyword = view.getSearchKeyword();
			String category = view.getSelectedCategory();
			refreshTable(keyword, category);
		}
	}

	private class BookButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedId = view.getSelectedGigId();
			String title = view.getSelectedGigTitle();
			double price = view.getSelectedGigPrice();

			if (selectedId == -1 || title == null) {
				JOptionPane.showMessageDialog(view, "Please highlight a service listing row from the table first.",
						"Selection Required", JOptionPane.WARNING_MESSAGE);
				return;
			}

			dashboard.openCheckoutWindow(selectedId, title, price);
		}
	}

}