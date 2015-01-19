package it.seat.visualzoom.zoom.gui.actions;

import it.seat.visualzoom.player.Movie;
import it.seat.visualzoom.player.MoviePart;
import it.seat.visualzoom.player.effects.CyclicScaleEffect;
import it.seat.visualzoom.player.layers.FixedLayer;
import it.seat.visualzoom.player.layers.Layer;
import it.seat.visualzoom.player.layers.MapLayer;
import it.seat.visualzoom.zoom.data.AbstractImageLoader;
import it.seat.visualzoom.zoom.data.ImageLoader;
import it.seat.visualzoom.zoom.geocoding.Location;
import it.seat.visualzoom.zoom.gui.MainWindow;
import it.seat.visualzoom.zoom.gui.NewProjectDialog;
import it.seat.visualzoom.zoom.gui.ProgressDialog;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;

public class NewMovieAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		MainWindow mainWindow = MainWindow.getInstance();

		// chiedo di chiudere l'eventuale player... per ora solo 1 progetto
		// per volta!
		mainWindow.closeMovie();
		NewProjectDialog npd = new NewProjectDialog();
		npd.addListener(mainWindow);
		npd.setLocationRelativeTo(mainWindow);
		npd.setModal(true);
		npd.setVisible(true);

		// quando chiudo il dialog
		Location location = mainWindow.getCurrentLocation();
		if (location != null) {
			ProgressDialog progress = new ProgressDialog();
			progress.setModal(true);
			progress.setTitle("Carico le immagini...");
			progress.setLocationRelativeTo(mainWindow);
//			progress.setStringPainted(true);
			progress.setProgress(0);
			mainWindow.setProgress(progress);
			progress.addListener(mainWindow);
			
			int movieWidth = mainWindow.getMovieWidth();
			int movieHeight = mainWindow.getMovieHeight();
			float lon = location.getLon();
			float lat = location.getLat();
			int zStart = mainWindow.getZStart();
			int zEnd = mainWindow.getZEnd();
			int movieLength = mainWindow.getMovieLength();
			int framerate = mainWindow.getFramerate();
			
			int dim = zStart - zEnd + 1;
			MapLayer mapLayer = new MapLayer(movieWidth*2, movieHeight*2, dim);
			mapLayer.addEffect(new CyclicScaleEffect(mapLayer, zStart, zEnd));
			
			Layer pgLayer = null, telespazioLayer = null;
			try {
				pgLayer = new FixedLayer(0, 0);
				pgLayer.setImage(ImageIO.read(new File("icons/logo_visual.png")));
				pgLayer.setX(movieWidth - pgLayer.getImage().getWidth() - 10);
				pgLayer.setY(10);

				telespazioLayer = new FixedLayer(0, 0);
				telespazioLayer.setImage(ImageIO.read(new File("icons/logo_telespazio_bianco.png")));
				telespazioLayer.setX(5);
				telespazioLayer.setY(10);
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			
			MoviePart moviePart = new MoviePart(1000*movieLength);
			moviePart.addLayer(mapLayer);
			
			Movie movie = new Movie(movieWidth*2, movieHeight*2, framerate);
			movie.addMoviePart(moviePart);
			
			movie.addLayer(pgLayer);
			movie.addLayer(telespazioLayer);

			mainWindow.setMovie(movie);

			AbstractImageLoader imageLoader = new ImageLoader(lat, lon, movieWidth*2, movieHeight*2, zStart, zEnd);
			imageLoader.addListener(mapLayer);
			imageLoader.addListener(mainWindow);
			imageLoader.addListener(progress);
			mainWindow.setLoadingMode();
			imageLoader.start();
			progress.setVisible(true);
		}
	}

	/*
	 * private Location checkEE() {
	 * if(searchData.getComune().equals("cerchi") &&
	 * searchData.getProvincia().equals("nel") &&
	 * searchData.getIndirizzo().equals("grano")) { return new
	 * Location("CIRCLES","IN","CORN","666","83457",43.6987f,10.44005f);
	 * }else if(searchData.getComune().equals("elicottero") &&
	 * searchData.getProvincia().equals("in") &&
	 * searchData.getIndirizzo().equals("volo")) { return new
	 * Location("HELI","COP","TER","113","581RR0",41.90645f,12.48995f);
	 * }else if(searchData.getComune().equals("gladiatori") &&
	 * searchData.getProvincia().equals("contro") &&
	 * searchData.getIndirizzo().equals("tigri")) { return new
	 * Location("COLOSSEO","","","0","0",41.89033f,12.49229f); }else return
	 * null; }
	 */
}
