package it.seat.visualzoom.meteo;

import it.seat.visualzoom.player.IMoviePlayer;
import it.seat.visualzoom.player.Movie;
import it.seat.visualzoom.player.MoviePart;
import it.seat.visualzoom.player.MoviePlayer;
import it.seat.visualzoom.player.effects.ScaleEffect;
import it.seat.visualzoom.player.effects.TransparencyEffect;
import it.seat.visualzoom.player.layers.FixedLayer;
import it.seat.visualzoom.player.layers.Layer;
import it.seat.visualzoom.player.layers.MeteoLayerFactory;
import it.seat.visualzoom.player.layers.SquareLayer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import meteo.bean.xml.Location;

public class Meteo {
	public static void main(String[] args) {
		JFrame frame = new JFrame("MeteoPlayer");

		/* attributi del player */
		int width = 720;
		int height = 540;
		int frameRate = 25;

		/* attributi del filmato */
		// Roma
		// float lon = 12.49f;
		// float lat = 41.89f;
		// Rossano Calabro
		 float lon = 16.63473f;
		 float lat = 39.57589f;
		// Milano
		// float lon = 9.1223f;
		// float lat = 45.4986f;
		// zoom iniziale e finale
		int zStart = 16;
		int zEnd = 13;

		/* attributi del meteo */
		int day = 0;
		int dayPart = 1;

		MeteoLayerFactory mlf = MeteoLayerFactory.getInstance();
		Location[] locations = mlf.getLocations(lon, lat, width * 2,
				height * 2, zEnd);

		Layer pgLayer = null, telespazioLayer = null;
		try {
			pgLayer = new FixedLayer(0, 0);
			pgLayer.setImage(ImageIO.read(new File("icons/logo_visual.png")));
			pgLayer.setX(width - pgLayer.getImage().getWidth() - 10);
			pgLayer.setY(10);

			telespazioLayer = new FixedLayer(0, 0);
			telespazioLayer.setImage(ImageIO.read(new File(
					"icons/logo_telespazio_bianco.png")));
			telespazioLayer.setX(5);
			telespazioLayer.setY(10);
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		BufferedImage linkedImage = null;
		try {
			linkedImage = ImageIO.read(new File("icons/logo_visual.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int linkedImageZ = Math.max(linkedImage.getWidth(), linkedImage
				.getHeight());

		/* inizio creazione del filmato */
		Movie movie = new Movie(width * 2, height * 2, frameRate);
		MeteoMoviePartFactory mmpf = MeteoMoviePartFactory.getInstance();

		/* zoomata in avanti */
		MoviePart zoomInMoviePart = mmpf.createZoomInMeteoMoviePart(width * 2,
				height * 2, lon, lat, zStart, zEnd, 2000, locations, day,
				dayPart);
		// layer ancorato che zooma in avanti
		Layer zoomInImageLayer = new SquareLayer(0, 0);
		zoomInImageLayer.setImage(linkedImage);
		zoomInImageLayer.setWidth(zoomInImageLayer.getWidth() * 2);
		zoomInImageLayer.setHeight(zoomInImageLayer.getHeight() * 2);
		zoomInImageLayer.setZ(linkedImageZ);
		zoomInImageLayer.addEffect(new ScaleEffect(zoomInImageLayer, zStart,
				zEnd));
		zoomInImageLayer.addEffect(new TransparencyEffect(zoomInImageLayer,
				IMoviePlayer.BACKWARD));
		zoomInMoviePart.addLayer(zoomInImageLayer);
		movie.addMoviePart(zoomInMoviePart);
		System.out.println("Zoom in movie part created");

		/* pausa */
		MoviePart stillMoviePart1 = mmpf.createStillMeteoMoviePart(width * 2,
				height * 2, lon, lat, zEnd, 2000, locations, day, dayPart);
		movie.addMoviePart(stillMoviePart1);
		System.out.println("First still movie part created");

		/* flip delle icone */
		// MoviePart flipMoviePart = mmpf.createFlippingMeteoMoviePart(width*2,
		// height*2, lon, lat, zEnd, 1000, locations, day, dayPart, day,
		// dayPart+1);
		// movie.addMoviePart(flipMoviePart);
		// System.out.println("Flip movie part created");
		/* inizio flipping */
		MoviePart startFlipMoviePart = mmpf.createStartFlippingMeteoMoviePart(
				width * 2, height * 2, lon, lat, zEnd, 1000, locations, day,
				dayPart);
		movie.addMoviePart(startFlipMoviePart);
		System.out.println("Start flip movie part created");

		/* fine flipping */
		MoviePart endFlipMoviePart = mmpf.createEndFlippingMeteoMoviePart(
				width * 2, height * 2, lon, lat, zEnd, 1000, locations,
				day + 1, dayPart);
		movie.addMoviePart(endFlipMoviePart);
		System.out.println("End flip movie part created");

		/* pausa */
		MoviePart stillMoviePart2 = mmpf.createStillMeteoMoviePart(width * 2,
				height * 2, lon, lat, zEnd, 2000, locations, day + 1, dayPart);
		movie.addMoviePart(stillMoviePart2);
		System.out.println("Second still movie part created");

		/* zoomata all'indietro */
		MoviePart zoomOutMoviePart = mmpf.createZoomOutMeteoMoviePart(
				width * 2, height * 2, lon, lat, zEnd, zStart, 2000, locations,
				day + 1, dayPart);
		// layer ancorato che zooma all'indietro
		Layer zoomOutImageLayer = new SquareLayer(0, 0);
		zoomOutImageLayer.setImage(linkedImage);
		zoomOutImageLayer.setWidth(zoomOutImageLayer.getWidth() * 2);
		zoomOutImageLayer.setHeight(zoomOutImageLayer.getHeight() * 2);
		zoomOutImageLayer.setZ(linkedImageZ);
		zoomOutImageLayer.addEffect(new ScaleEffect(zoomOutImageLayer, zEnd,
				zStart));
		zoomOutImageLayer.addEffect(new TransparencyEffect(zoomOutImageLayer,
				IMoviePlayer.FORWARD));
		zoomOutMoviePart.addLayer(zoomOutImageLayer);
		movie.addMoviePart(zoomOutMoviePart);
		System.out.println("Zoom out movie part created");

		/* layer fissi */
		movie.addLayer(pgLayer);
		movie.addLayer(telespazioLayer);

		MoviePlayer moviePlayer = new MoviePlayer(movie, width, height);
		// moviePlayer.setCaptureMode(true);
		frame.add(moviePlayer);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				new Thread(new Runnable() {
					public void run() {
						System.exit(0);
					}
				}).start();
			}
		});
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
