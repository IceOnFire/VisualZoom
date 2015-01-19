/*
 * Creato il 11-apr-2007
 *
 */
package it.seat.visualzoom.player;

import it.seat.visualzoom.zoom.data.AbstractImageLoader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * @author Antony
 */
public class SerialImageLoader extends AbstractImageLoader {
	private Loader[] loaders;

	public SerialImageLoader(float lon, float lat, int width, int height,
			int zStart, int zEnd) {
		super(lon, lat, width, height, zStart, zEnd);
	}

	@Override
	public Vector<BufferedImage> loadImages() {
		Vector<BufferedImage> images = new Vector<BufferedImage>();
		this.stop = false;
		loaders = new Loader[zoomLevels.length];
		for (int i = 0; i < zoomLevels.length; i++) {
			loaders[i] = new Loader(i);
			loaders[i].start();
		}
		
		// Attende che tutti i thread finiscano di caricare
		for (int i = 0; i < zoomLevels.length; i++) {
			try {
				loaders[i].join();
				images.add(0, loaders[i].getImage());
			} catch (InterruptedException e) {
			}
		}
		if (stop)
			return null;
		else
			return images;
	}

	@Override
	public void stopLoading() {
		if (loaders != null && loaders.length > 0) {
			for (int i = 0; i < loaders.length; i++) {
				loaders[i].stopLoading();
			}
		}
		this.stop = true;
	}

	class Loader extends Thread {
		private int imageNumber;
		private boolean stop;
		private BufferedImage image;

		public Loader(int imageNumber) {
			super();
			this.imageNumber = imageNumber;
		}

		public void run() {
			this.stop = false;
			URL url;
			try {
				url = new URL(
						"http://mapserver.tuttocitta.it/readdll/MapRender.aspx?x="
								+ lat + "&y=" + lon + "&z=0.3&xpix=" + width
								+ "&ypix=" + height
								+ "&lx=0&ly=0&quantization=Palette&tit=&dz="
								+ SerialImageLoader.this.zoomLevels[imageNumber]
								+ "&sm=orto&fi=jpeg&sito=vz");//&sito=vzoom");
				URLConnection con = url.openConnection(proxyPrefs.getSelectedProxy());
				if (proxyPrefs.isAuthRequired()) {
					con.setRequestProperty("Proxy-Authorization", proxyPrefs.getProxyAuthString());
				}
				con.setConnectTimeout(5000);
				InputStream is = con.getInputStream();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				int len;
				byte[] buf = new byte[1024];
				while ((len = is.read(buf)) > 0 && !stop) {
					os.write(buf, 0, len);
				}
				if (!stop) {
					image = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
				}
			} catch (Exception e) {
			}
		}

		public BufferedImage getImage() {
			return image;
		}
		
		public void stopLoading() {
			this.stop = true;
		}
	}
	
	public static void main(String[] args) {
		SerialImageLoader imageLoader = new SerialImageLoader(12.49f, 41.89f, 1440, 1080, 16, 9);
		Vector<BufferedImage> images = imageLoader.loadImages();
		for (BufferedImage image : images) {
			System.out.println(image);
		}
	}
}
