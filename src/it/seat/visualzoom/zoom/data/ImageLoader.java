/*
 * Creato il 11-apr-2007
 *
 */
package it.seat.visualzoom.zoom.data;

import it.seat.visualzoom.logger.Log;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * @author Leo
 * 
 */
public class ImageLoader extends AbstractImageLoader {
	private Loader[] loaders;
	
	public ImageLoader(float lon, float lat, int width, int height,
			int zStart, int zEnd) {
		super(lon, lat, width, height, zStart, zEnd);
	}

	public void run() {
		loadImages();
	}
	
	@Override
	public Vector<BufferedImage> loadImages() {
		stop = false;
		loaders = new Loader[zoomLevels.length];
		for (int i = 0; i < zoomLevels.length; i++) {
			loaders[i] = new Loader(i);
			loaders[i].start();
		}
		// Attende che tutti i thread finiscano di caricare
		for (int i = 0; i < zoomLevels.length; i++) {
			try {
				loaders[i].join();
			} catch (InterruptedException e) {
			}
		}
		if (stop)
			notifyLoadingCancelled();
		else
			notifyLoadingComplete();
		
		return null;
	}

	@Override
	public void stopLoading() {
		if (loaders != null && loaders.length > 0) {
			for (int i = 0; i < loaders.length; i++) {
				loaders[i].stopImageLoading();
			}
		}
		this.stop = true;
	}

	class Loader extends Thread {

		private int imageNumber;
		private boolean stop;

		public Loader(int imageNumber) {
			super();
			this.imageNumber = imageNumber;
		}

		public void run() {
			this.stop = false;
			URL url;
			try {
				// ROMA:
				// http://mapserver.tuttocitta.it/readdll/MapRender.aspx?x=12.5&y=41.9&z=0.3&xpix=1440&ypix=1080&lx=0&ly=0&quantization=Palette&tit=&dz=0.5&sm=orto&fi=jpeg&sito=vzoom
				url = new URL(
						"http://mapserver.tuttocitta.it/readdll/MapRender.aspx?x="
								+ lon + "&y=" + lat + "&z=0.3&xpix=" + width
								+ "&ypix=" + height
								+ "&lx=0&ly=0&quantization=Palette&tit=&dz="
								+ ImageLoader.this.zoomLevels[imageNumber]
								+ "&sm=orto&fi=jpeg&sito=vz");//&sito=vzoom");
				// url = new
				// URL("http://maprender-l1a.pgol.com/readdll/MapRender.aspx?x="+lon+"&y="+lat+"&z=0.3&xpix="+width+"&ypix="+height+"&lx=0&ly=0&quantization=Palette&tit=&dz="+ImageLoader.this.zoomLevels[imageNumber]+"&sm=orto&fi=jpeg&sito=vzoom");
				// url = new
				// URL("http://mapserver.tuttocitta.it/visualzoom/MapRender.aspx?x="+lon+"&y="+lat+"&z=0.3&xpix="+width+"&ypix="+height+"&lx=0&ly=0&quantization=Palette&tit=&dz="+ImageLoader.this.zoomLevels[imageNumber]+"&sm=orto&fi=jpeg");
				// System.out.println(url.toString());
				// url = new
				// URL("http://192.168.40.20/routec/MapRender.aspx?x="+lon+"&y="+lat+"&z=0.3&xpix="+width+"&ypix="+height+"&lx=0&ly=0&quantization=Palette&tit=&dz="+ZoomApplet.ZOOM[imageNumber]+"&sm=orto&fi=jpeg");
				// url = new
				// URL("http://mapserver.tuttocitta.it/readdll/MapRender.aspx?x="+lon+"&y="+lat+"&z="+ZoomApplet.ZOOM[imageNumber]+"&xpix="+width+"&ypix="+height+"&lx=0&ly=0&quantization=Palette&tit=&dz=1&sm=orto&fi=jpeg");
				URLConnection con = url.openConnection(proxyPrefs
						.getSelectedProxy());
				Log.getLogger().finer(
						"Proxy selected: " + proxyPrefs.getSelectedProxy());
				Log.getLogger()
						.finer(
								"Proxy Auth String: "
										+ proxyPrefs.getProxyAuthString());
				if (proxyPrefs.isAuthRequired()) {
					con.setRequestProperty("Proxy-Authorization", proxyPrefs
							.getProxyAuthString());
				}
				con.setConnectTimeout(5000);
				InputStream is = con.getInputStream();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				int len;
				byte[] buf = new byte[1024];
				while ((len = is.read(buf)) > 0) {
					if (stop)
						break;
					os.write(buf, 0, len);
				}
				if (!stop) {
					BufferedImage img = ImageIO.read(new ByteArrayInputStream(
							os.toByteArray()));
					notifyImageLoaded(imageNumber, img);
					notifyImageLoaded(imageNumber);
				}
				// BufferedImage img = ImageIO.read(url);
			} catch (Exception e) {
				notifyError(imageNumber, e);
				e.printStackTrace();
			}
		}

		public void stopImageLoading() {
			this.stop = true;
		}
	}
}
