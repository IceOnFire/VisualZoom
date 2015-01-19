package it.seat.visualzoom.zoom.gui.actions;

import it.seat.visualzoom.player.MoviePlayer;
import it.seat.visualzoom.zoom.gui.MainWindow;
import it.seat.visualzoom.zoom.gui.MoviePreview;
import it.seat.visualzoom.zoom.gui.ProgressDialog;
import it.seat.visualzoom.zoom.video.JpegImagesToMovie;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class SaveMovieAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		MainWindow mainWindow = MainWindow.getInstance();
		
		JFileChooser fc = new JFileChooser(new File("zoom.mov"));
		fc.addChoosableFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.getName().endsWith(".mov"))
					return true;
				return false;
			}

			public String getDescription() {
				return "Quicktime (.mov)";
			}
		});
		fc.showSaveDialog(mainWindow);
		File selFile = fc.getSelectedFile();
		if (selFile != null) {
			if (!selFile.getName().endsWith(".mov")
					&& !selFile.getName().endsWith(".MOV")) {
				selFile = new File(selFile.getAbsolutePath() + ".mov");
			}
			
//			MovieWriter movieWriter = new MovieWriter(
//					(MoviePlayer) ((MoviePreview) mainWindow.getPreview())
//					.getMoviePlayer(), selFile);
//			mainWindow.setMovieWriter(movieWriter);
//			ProgressDialog progress = new ProgressDialog();
////			mainWindow.setProgress(progress);
//			movieWriter.addListener(mainWindow);
////			movieWriter.addListener(progress);
//			progress.setTitle("Salvataggio in corso...");
//			progress.addListener(mainWindow);
//			progress.setLocationRelativeTo(mainWindow);
//			progress.setProgress(0);
////			progress.setModal(true);
//			mainWindow.setSavingMode();
//			movieWriter.start();
////			progress.setVisible(true);
			
//			MoviePlayer player = (MoviePlayer) ((MoviePreview) mainWindow.getPreview()).getMoviePlayer();
//			new JpegImagesToMovie().doIt(player, selFile);
			
			MoviePlayer player = (MoviePlayer) ((MoviePreview) mainWindow.getPreview()).getMoviePlayer();
			JpegImagesToMovie jitm = new JpegImagesToMovie(player, selFile);

			jitm.addListener(mainWindow);
			mainWindow.setSavingMode();

			ProgressDialog progress = null;
			/* su Linux il dialog rovina l'immagine... */
			if (!System.getProperty("os.name").equals("Linux")) {
				progress = new ProgressDialog();
				progress.setModal(true);
				progress.setTitle("Salvataggio in corso...");
				progress.setLocationRelativeTo(mainWindow);
				progress.setProgress(0);
				mainWindow.setProgress(progress);
				jitm.addListener(progress);
				progress.addListener(mainWindow);
			}

			jitm.start();

			if (progress != null) {
				progress.setVisible(true);
			}
		}
	}
}
