/*
 * Creato il 8-mag-2007
 */
package it.seat.visualzoom.zoom;

import it.seat.visualzoom.logger.Log;
import it.seat.visualzoom.zoom.gui.MainWindow;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 * @author Leonardo Landini
 */
public class Zoom {

	private MainWindow window;

	public Zoom() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("No platform Look&Feel... using default.");
		}
		window = MainWindow.getInstance();
		window.setSize(new Dimension(Toolkit.getDefaultToolkit()
				.getScreenSize().width, Toolkit.getDefaultToolkit()
				.getScreenSize().height));
		window.setExtendedState(Frame.MAXIMIZED_BOTH);
		window.setTitle("VisualZoom 2.1 - SEAT Pagine Gialle S.p.A.");
		window.setIconImage(new ImageIcon("icons/visualzoom.gif").getImage());
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public void startApp() {
		Log.getLogger().info("Start Application");
		window.setVisible(true);
	}

	public static void main(String[] args) {
		new Zoom().startApp();
	}
}
