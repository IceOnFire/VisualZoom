package it.seat.visualzoom.zoom.gui.actions;

import it.seat.visualzoom.zoom.gui.MainWindow;
import it.seat.visualzoom.zoom.gui.TitleDialog;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AddTitleAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		// aggiunge al vettore degli oggetti di testo e inizializza il
		// dialog delle preferenze
		new TitleDialog(MainWindow.getInstance()).setVisible(true);
	}
}
