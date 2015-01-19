package it.seat.visualzoom.zoom.gui.actions;

import it.seat.visualzoom.zoom.gui.MainWindow;
import it.seat.visualzoom.zoom.gui.MoviePreview;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ShowPreviewAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		MoviePreview preview = MainWindow.getInstance().getPreview();
		if (preview != null && !preview.isShowing()) {
			preview.setVisible(true);
		}
	}
}
