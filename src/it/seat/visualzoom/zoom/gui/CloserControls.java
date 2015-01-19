/*
 * Creato il 19-giu-2007
 */
package it.seat.visualzoom.zoom.gui;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * @author Leonardo Landini
 */
public class CloserControls extends JPanel {

	private JRadioButton mapButton = null;
	private JRadioButton ortoButton = null;
	private JRadioButton mixedButton = null;
	private ButtonGroup bgroup = null;

	/**
	 * This is the default constructor
	 */
	public CloserControls() {
		super();
		bgroup = new ButtonGroup();
		bgroup.add(getMapButton());
		bgroup.add(getOrtoButton());
		bgroup.add(getMixedButton());
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(198, 33);
		this.add(getMapButton(), null);
		this.add(getOrtoButton(), null);
		this.add(getMixedButton(), null);
	}

	/**
	 * This method initializes jRadioButton
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getMapButton() {
		if (mapButton == null) {
			mapButton = new JRadioButton();
			mapButton.setText("Mappa");
		}
		return mapButton;
	}

	/**
	 * This method initializes jRadioButton1
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getOrtoButton() {
		if (ortoButton == null) {
			ortoButton = new JRadioButton();
			ortoButton.setText("Foto");
		}
		return ortoButton;
	}

	/**
	 * This method initializes jRadioButton2
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getMixedButton() {
		if (mixedButton == null) {
			mixedButton = new JRadioButton();
			mixedButton.setText("Mista");
		}
		return mixedButton;
	}
}
