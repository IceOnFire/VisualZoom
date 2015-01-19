/*
 * Creato il 22-mag-2007
 */
package it.seat.visualzoom.zoom.gui;

import it.seat.visualzoom.zoom.geocoding.Location;
import it.seat.visualzoom.zoom.gui.event.LocationSelectListener;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author Leonardo Landini
 */
public class LocationSelectDialog extends JDialog {

	private ArrayList listeners = new ArrayList();

	private javax.swing.JPanel jContentPane = null;

	private JPanel buttonPanel = null;
	private JList locationList = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JScrollPane jScrollPane = null;

	/**
	 * This is the default constructor
	 */
	public LocationSelectDialog() {
		super();
		Location l = new Location("Milano", "", "MI", "32222", "ds");
		Location[] list = new Location[1];
		list[0] = l;
		initialize(list);
	}

	public LocationSelectDialog(Location[] locations) {
		super();
		initialize(locations);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize(Location[] locations) {
		this.setTitle("Seleziona una localit�...");
		this.setName("locationSelectDialog");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.getLocationList().setListData(locations);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.add(getOkButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jList
	 * 
	 * @return javax.swing.JList
	 */
	private JList getLocationList() {
		if (locationList == null) {
			locationList = new JList();
			locationList.setCellRenderer(new MyCellRenderer());
			locationList
					.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		}
		return locationList;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			// okButton.setIcon(new
			// ImageIcon(getClass().getResource("/zoom/icons/b_ok.png")));
			// okButton.setBorderPainted(false);
			// okButton.setContentAreaFilled(false);
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fireLocationSelectedEvent((Location) getLocationList()
							.getSelectedValue());
					LocationSelectDialog.this.dispose();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Annulla");
			// cancelButton.setIcon(new
			// ImageIcon(getClass().getResource("/zoom/icons/b_annulla.png")));
			// cancelButton.setBorderPainted(false);
			// cancelButton.setContentAreaFilled(false);
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fireCancelSelectEvent();
					LocationSelectDialog.this.dispose();
				}
			});
		}
		return cancelButton;
	}

	class MyCellRenderer extends DefaultListCellRenderer {

		/*
		 * This is the only method defined by ListCellRenderer. We just
		 * reconfigure the Jlabel each time we're called.
		 */
		public Component getListCellRendererComponent(JList list, Object value, // value
																				// to
																				// display
				int index, // cell index
				boolean iss, // is the cell selected
				boolean chf) // the list and the cell have the focus
		{
			/*
			 * Cambio solo la formattazione di value
			 */
			String text = this.format((Location) value);
			return super.getListCellRendererComponent(list, text, index, iss,
					chf);
		}

		private String format(Location l) {
			String result = l.getCom();
			if (l.getAddress().length() > 0) {
				result += " - " + l.getAddress();
				if (l.getCiv().length() > 0) {
					result += ", " + l.getCiv();
				}
			}
			return result;
		}
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getLocationList());
		}
		return jScrollPane;
	}

	/*
	 * Metodi di utilit�
	 * 
	 */

	public void addListener(LocationSelectListener listener) {
		this.listeners.add(listener);
	}

	private void fireLocationSelectedEvent(Location l) {
		Iterator it = this.listeners.iterator();
		while (it.hasNext()) {
			((LocationSelectListener) it.next()).onLocationSelected(l);
		}
	}

	private void fireCancelSelectEvent() {
		Iterator it = this.listeners.iterator();
		while (it.hasNext()) {
			((LocationSelectListener) it.next()).onLocationSelectCancelled();
		}
	}
}
