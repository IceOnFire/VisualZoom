package it.seat.visualzoom.zoom.data;

import it.seat.visualzoom.zoom.utils.ProxyPrefs;

import java.awt.image.BufferedImage;
import java.util.Vector;

public abstract class AbstractImageLoader extends Thread {
	protected float lat, lon;
	protected int width, height;
	protected int[] zoomLevels;
	protected boolean stop;
	protected ProxyPrefs proxyPrefs;
	private Vector<ImageLoaderListener> listeners;
	
	public AbstractImageLoader(float lat, float lon, int width, int height,
			int zStart, int zEnd) {
		this.lat = lat;
		this.lon = lon;
		this.width = width;
		this.height = height;
		listeners = new Vector<ImageLoaderListener>();
		
		int z0 = Math.min(zStart, zEnd);
		int Z = Math.max(zStart, zEnd);
		zoomLevels = new int[(Z - z0) + 1];

		for (int i = z0; i <= Z; i++) {
			zoomLevels[i - z0] = (int)Math.pow(2, i - 4);
		}
		
		proxyPrefs = ProxyPrefs.getInstance();
	}
	
	public abstract Vector<BufferedImage> loadImages();

	public abstract void stopLoading();
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}
	
	public void addListener(ImageLoaderListener listener) {
		listeners.add(listener);
	}
	
	protected void notifyImageLoaded(int n, BufferedImage img) {
		for (ImageLoaderListener listener : listeners) {
			listener.onImageLoaded(n, img);
		}
	}

	protected void notifyImageLoaded(int n) {
		for (ImageLoaderListener listener : listeners) {
			listener.onImageLoaded(n, zoomLevels.length);
		}
	}

	protected void notifyError(int n, Exception e) {
		for (ImageLoaderListener listener : listeners) {
			listener.onError(n, e);
		}
	}

	public void notifyLoadingComplete() {
		for (ImageLoaderListener listener : listeners) {
			listener.onLoadingComplete();
		}
	}

	protected void notifyLoadingCancelled() {
		for (ImageLoaderListener listener : listeners) {
			listener.onLoadingCancelled();
		}
	}
}