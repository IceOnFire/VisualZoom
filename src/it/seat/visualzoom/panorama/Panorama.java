package it.seat.visualzoom.panorama;

import it.seat.visualzoom.player.Movie;
import it.seat.visualzoom.player.MoviePart;
import it.seat.visualzoom.player.layers.CylinderLayer;
import it.seat.visualzoom.player.layers.Layer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Panorama {
	public static void main(String[] args) {
		JFrame frame = new JFrame("PanoramaPlayer");

		int width = 640;
		int height = 480;
		int frameRate = 25;
		Movie movie = new Movie(width, height, frameRate);

		/** Roma */
//		float lon = 12.49f;
//		float lat = 41.89f;
		/** Rossano */
//		float lon = 16.63473f;
//		float lat = 39.57589f;
		/** Milano */
//		float lon = 9.191567f;
//		float lat = 45.464044f;
//		int zStart = 4;
//		int zEnd = 12;

		MoviePart moviePart1 = new MoviePart(2000);
		movie.addMoviePart(moviePart1);

		BufferedImage image1 = null;
		try {
			image1 = ImageIO.read(new File("icons/panorama/IMG_3985.jpg"));
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		/* layer cilindrico */
		Layer layer1 = new CylinderLayer(0, 0, height, height);
		layer1.setImage(image1);
		layer1.setZ(0.010f);
		moviePart1.addLayer(layer1);

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
