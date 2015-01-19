package it.seat.visualzoom.zoom.gui.actions;

import it.seat.visualzoom.zoom.gui.ProxyDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class OpenProxyDialogAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		new ProxyDialog().setVisible(true);
	}
}
