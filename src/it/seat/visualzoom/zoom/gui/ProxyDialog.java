/*
 * Creato il 24-mag-2007
 */
package it.seat.visualzoom.zoom.gui;

import it.seat.visualzoom.logger.Log;
import it.seat.visualzoom.zoom.utils.PBECipher;
import it.seat.visualzoom.zoom.utils.ProxyPrefs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;

/**
 * @author Leonardo Landini
 */
public class ProxyDialog extends JDialog {

	private javax.swing.JPanel jContentPane = null;

	// radio buttons
	private JRadioButton directConnectionRadio = null;
	private JRadioButton proxyConnectionRadio = null;
	private ButtonGroup buttonGroup = null;
	// check boxes
	private JCheckBox proxyAuthRequiredBox = null;
	// text fields
	private JTextField proxyHostField = null;
	private JTextField proxyPortField = null;
	private JTextField proxyUser = null;
	private JTextField proxyPass = null;
	// labels
	private JLabel titleLabel = null;
	private JLabel directConnectionLabel = null;
	private JLabel proxyConnectionLabel = null;
	private JLabel proxyLabel = null;
	private JLabel portLabel = null;
	private JLabel userLabel = null;
	private JLabel passLabel = null;
	private JLabel proxyAuthLabel = null;
	// buttons
	private JButton okButton = null;
	private JButton cancelButton = null;
	// panels
	private JPanel dataPanel = null;
	private JPanel buttonPanel = null;
	private JPanel titlePanel = null;
	// ProxyPrefs
	private ProxyPrefs proxyPrefs;

	/**
	 * This is the default constructor
	 */
	public ProxyDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Impostazioni di connessione");
		this.setName("ProxyPrefs");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(500, 320);
		this.setModal(true);
		this.setContentPane(getJContentPane());
		this.buttonGroup = new ButtonGroup();
		this.buttonGroup.add(getDirectConnectionRadio());
		this.buttonGroup.add(getProxyConnectionRadio());
		initState();
	}

	private void initState() {
		// inizializza lo stato dei bottoni, deve leggere da file i valori
		// salvati!
		proxyPrefs = ProxyPrefs.getInstance();
		boolean proxySet = Boolean.parseBoolean(proxyPrefs
				.get(ProxyPrefs.PROXY_SET));
		boolean proxyAuthRequired = Boolean.parseBoolean(proxyPrefs
				.get(ProxyPrefs.PROXY_AUTH_REQUIRED));
		String host = proxyPrefs.get(ProxyPrefs.PROXY_HOST);
		String user = proxyPrefs.get(ProxyPrefs.PROXY_USER);
		int port = 0;
		try {
			port = Integer.parseInt(proxyPrefs.get(ProxyPrefs.PROXY_PORT));
		} catch (Exception e) {
			Log.getLogger().info(
					"La porta HTTP deve essere un numero tra 0 e 65534 ["
							+ e.toString() + "]");
		}
		String pass = "";
		try {
			pass = PBECipher.decrypt(proxyPrefs.get(ProxyPrefs.PROXY_PASS));
		} catch (Exception e) {
			Log.getLogger().warning(
					"Impossibile decifrare la password di accesso al proxy ["
							+ e.toString() + "]");
		}
		getProxyHostField().setText(host);
		getPortField().setText(String.valueOf(port));
		getProxyUser().setText(user);
		getProxyPass().setText(pass);
		if (user != null && user.length() > 0)
			getProxyAuthRequiredBox().setSelected(proxyAuthRequired);
		if (proxySet)
			getProxyConnectionRadio().doClick();
		else
			getDirectConnectionRadio().doClick();
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
			jContentPane.add(getTitlePanel(), BorderLayout.NORTH);
			jContentPane.add(getDataPanel(), BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private JPanel getTitlePanel() {
		if (titlePanel == null) {
			titlePanel = new JPanel();
			titlePanel.add(getTitleLabel());
		}
		return titlePanel;
	}

	private JPanel getDataPanel() {
		if (dataPanel == null) {
			dataPanel = new JPanel();
			dataPanel.add(getDirectConnectionRadio());
			dataPanel.add(getDirectConnectionLabel());
			dataPanel.add(getProxyConnectionRadio());
			dataPanel.add(getProxyConnectionLabel());
			dataPanel.add(getProxyHostField());
			dataPanel.add(getPortField());
			dataPanel.add(getProxyLabel());
			dataPanel.add(getPortLabel());
			dataPanel.add(getProxyAuthRequiredBox());
			dataPanel.add(getProxyAuthLabel());
			dataPanel.add(getProxyUser());
			dataPanel.add(getUserLabel());
			dataPanel.add(getProxyPass());
			dataPanel.add(getPassLabel());

			SpringLayout layout = new SpringLayout();

			layout.putConstraint(SpringLayout.WEST, getDirectConnectionRadio(),
					20, SpringLayout.WEST, dataPanel);
			layout.putConstraint(SpringLayout.NORTH,
					getDirectConnectionRadio(), 20, SpringLayout.NORTH,
					dataPanel);

			layout.putConstraint(SpringLayout.WEST, getDirectConnectionLabel(),
					10, SpringLayout.EAST, getDirectConnectionRadio());
			layout.putConstraint(SpringLayout.NORTH,
					getDirectConnectionLabel(), 0, SpringLayout.NORTH,
					getDirectConnectionRadio());

			layout.putConstraint(SpringLayout.WEST, getProxyConnectionRadio(),
					20, SpringLayout.WEST, dataPanel);
			layout.putConstraint(SpringLayout.NORTH, getProxyConnectionRadio(),
					20, SpringLayout.SOUTH, getDirectConnectionRadio());

			layout.putConstraint(SpringLayout.WEST, getProxyConnectionLabel(),
					10, SpringLayout.EAST, getProxyConnectionRadio());
			layout.putConstraint(SpringLayout.NORTH, getProxyConnectionLabel(),
					0, SpringLayout.NORTH, getProxyConnectionRadio());

			layout.putConstraint(SpringLayout.WEST, getProxyLabel(), 40,
					SpringLayout.WEST, dataPanel);
			layout.putConstraint(SpringLayout.NORTH, getProxyLabel(), 10,
					SpringLayout.SOUTH, getProxyConnectionRadio());

			layout.putConstraint(SpringLayout.WEST, getProxyHostField(), 10,
					SpringLayout.EAST, getProxyLabel());
			layout.putConstraint(SpringLayout.NORTH, getProxyHostField(), 0,
					SpringLayout.NORTH, getProxyLabel());

			layout.putConstraint(SpringLayout.WEST, getPortLabel(), 20,
					SpringLayout.EAST, getProxyHostField());
			layout.putConstraint(SpringLayout.NORTH, getPortLabel(), 0,
					SpringLayout.NORTH, getProxyHostField());

			layout.putConstraint(SpringLayout.WEST, getPortField(), 10,
					SpringLayout.EAST, getPortLabel());
			layout.putConstraint(SpringLayout.NORTH, getPortField(), 0,
					SpringLayout.NORTH, getPortLabel());

			layout.putConstraint(SpringLayout.WEST, getProxyAuthRequiredBox(),
					0, SpringLayout.WEST, getProxyHostField());
			layout.putConstraint(SpringLayout.NORTH, getProxyAuthRequiredBox(),
					10, SpringLayout.SOUTH, getProxyHostField());

			layout.putConstraint(SpringLayout.WEST, getProxyAuthLabel(), 10,
					SpringLayout.EAST, getProxyAuthRequiredBox());
			layout.putConstraint(SpringLayout.NORTH, getProxyAuthLabel(), 0,
					SpringLayout.NORTH, getProxyAuthRequiredBox());

			layout.putConstraint(SpringLayout.WEST, getUserLabel(), 0,
					SpringLayout.WEST, getProxyLabel());
			layout.putConstraint(SpringLayout.NORTH, getUserLabel(), 10,
					SpringLayout.SOUTH, getProxyAuthRequiredBox());

			layout.putConstraint(SpringLayout.WEST, getProxyUser(), 0,
					SpringLayout.WEST, getProxyHostField());
			layout.putConstraint(SpringLayout.NORTH, getProxyUser(), 0,
					SpringLayout.NORTH, getUserLabel());

			layout.putConstraint(SpringLayout.WEST, getPassLabel(), 0,
					SpringLayout.WEST, getUserLabel());
			layout.putConstraint(SpringLayout.NORTH, getPassLabel(), 10,
					SpringLayout.SOUTH, getUserLabel());

			layout.putConstraint(SpringLayout.WEST, getProxyPass(), 0,
					SpringLayout.WEST, getProxyHostField());
			layout.putConstraint(SpringLayout.NORTH, getProxyPass(), 0,
					SpringLayout.NORTH, getPassLabel());

			dataPanel.setLayout(layout);
		}
		return dataPanel;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton());
			buttonPanel.add(getCancelButton());
		}
		return buttonPanel;
	}

	private JRadioButton getDirectConnectionRadio() {
		if (directConnectionRadio == null) {
			directConnectionRadio = new JRadioButton(
					new EnableDirectConnection());
		}
		return directConnectionRadio;
	}

	private JRadioButton getProxyConnectionRadio() {
		if (proxyConnectionRadio == null) {
			proxyConnectionRadio = new JRadioButton(new EnableProxyConnection());
		}
		return proxyConnectionRadio;
	}

	private JCheckBox getProxyAuthRequiredBox() {
		if (proxyAuthRequiredBox == null) {
			proxyAuthRequiredBox = new JCheckBox(new EnableAuth());
		}
		return proxyAuthRequiredBox;
	}

	private JTextField getProxyHostField() {
		if (proxyHostField == null) {
			proxyHostField = new JTextField(20);
		}
		return proxyHostField;
	}

	private JTextField getPortField() {
		if (proxyPortField == null) {
			proxyPortField = new JTextField(5);
		}
		return proxyPortField;
	}

	private JTextField getProxyUser() {
		if (proxyUser == null) {
			proxyUser = new JTextField(20);
		}
		return proxyUser;
	}

	private JTextField getProxyPass() {
		if (proxyPass == null) {
			proxyPass = new JPasswordField(20);
		}
		return proxyPass;
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel(
					"Configurazione del proxy per l'accesso a Internet");
		}
		return titleLabel;
	}

	private JLabel getDirectConnectionLabel() {
		if (directConnectionLabel == null) {
			directConnectionLabel = new JLabel(
					"Connessione diretta a Internet (nessun proxy)");
		}
		return directConnectionLabel;
	}

	private JLabel getProxyConnectionLabel() {
		if (proxyConnectionLabel == null) {
			proxyConnectionLabel = new JLabel(
					"Configurazione manuale del proxy");
		}
		return proxyConnectionLabel;
	}

	private JLabel getProxyLabel() {
		if (proxyLabel == null) {
			proxyLabel = new JLabel("Proxy HTTP:");
		}
		return proxyLabel;
	}

	private JLabel getPortLabel() {
		if (portLabel == null) {
			portLabel = new JLabel("Porta:");
		}
		return portLabel;
	}

	private JLabel getUserLabel() {
		if (userLabel == null) {
			userLabel = new JLabel("Username:");
		}
		return userLabel;
	}

	private JLabel getPassLabel() {
		if (passLabel == null) {
			passLabel = new JLabel("Password:");
		}
		return passLabel;
	}

	private JLabel getProxyAuthLabel() {
		if (proxyAuthLabel == null) {
			proxyAuthLabel = new JLabel("Il proxy richiede l'autenticazione");
		}
		return proxyAuthLabel;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton(new SavePrefs());
			okButton.setText("OK");
		}
		return okButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(new CancelPrefs());
			cancelButton.setText("Annulla");
		}
		return cancelButton;
	}

	public static void main(String[] args) {
		new ProxyDialog().setVisible(true);
	}

	// Actions
	class EnableDirectConnection extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			getProxyLabel().setEnabled(false);
			getProxyHostField().setEnabled(false);
			getPortLabel().setEnabled(false);
			getPortField().setEnabled(false);
			getProxyAuthLabel().setEnabled(false);
			getProxyAuthRequiredBox().setEnabled(false);
			getUserLabel().setEnabled(false);
			getProxyUser().setEnabled(false);
			getPassLabel().setEnabled(false);
			getProxyPass().setEnabled(false);
		}
	}

	class EnableProxyConnection extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			getProxyLabel().setEnabled(true);
			getProxyHostField().setEnabled(true);
			getPortLabel().setEnabled(true);
			getPortField().setEnabled(true);
			getProxyAuthLabel().setEnabled(true);
			getProxyAuthRequiredBox().setEnabled(true);
			getUserLabel().setEnabled(getProxyAuthRequiredBox().isSelected());
			getProxyUser().setEnabled(getProxyAuthRequiredBox().isSelected());
			getPassLabel().setEnabled(getProxyAuthRequiredBox().isSelected());
			getProxyPass().setEnabled(getProxyAuthRequiredBox().isSelected());
		}
	}

	class EnableAuth extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			getUserLabel().setEnabled(getProxyAuthRequiredBox().isSelected());
			getProxyUser().setEnabled(getProxyAuthRequiredBox().isSelected());
			getPassLabel().setEnabled(getProxyAuthRequiredBox().isSelected());
			getProxyPass().setEnabled(getProxyAuthRequiredBox().isSelected());
		}
	}

	class SavePrefs extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			proxyPrefs.set(ProxyPrefs.PROXY_SET, String
					.valueOf(getProxyConnectionRadio().isSelected()));
			proxyPrefs
					.set(ProxyPrefs.PROXY_HOST, getProxyHostField().getText());
			proxyPrefs.set(ProxyPrefs.PROXY_PORT, getPortField().getText());
			proxyPrefs.set(ProxyPrefs.PROXY_AUTH_REQUIRED, String
					.valueOf(getProxyAuthRequiredBox().isSelected()));
			proxyPrefs.set(ProxyPrefs.PROXY_USER, getProxyUser().getText());
			try {
				proxyPrefs.set(ProxyPrefs.PROXY_PASS, PBECipher
						.encrypt(getProxyPass().getText()));
			} catch (Exception exc) {
				Log.getLogger().severe(
						"Impossibile cifrare la password per il proxy ["
								+ exc.toString() + "]");
			}
			proxyPrefs.save();
			setVisible(false);
		}
	}

	class CancelPrefs extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	}
}
