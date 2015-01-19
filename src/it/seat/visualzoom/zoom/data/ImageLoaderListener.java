/*
 * Creato il 11-apr-2007
 */
package it.seat.visualzoom.zoom.data;

import java.awt.image.BufferedImage;


/**
 * @author Leo
 */
public interface ImageLoaderListener {

	public void onImageLoaded(int imageNumber, int totImages);
	
	public void onImageLoaded(int imageNumber, BufferedImage img);

	public void onLoadingComplete();

	public void onLoadingCancelled();

	public void onError(int imageNumber, Exception e);

}
