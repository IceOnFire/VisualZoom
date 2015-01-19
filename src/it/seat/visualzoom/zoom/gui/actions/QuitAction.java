package it.seat.visualzoom.zoom.gui.actions;

import it.seat.visualzoom.zoom.gui.MainWindow;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class QuitAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		MainWindow.getInstance().dispose();
		System.exit(0);
	}
}
