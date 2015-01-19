/*
 * Creato il 11-apr-2007
 */
package it.seat.visualzoom.zoom.video;

/**
 * @author Leo
 */
public interface MovieWriterListener {

	public void onProgress(int perc);

	public void onWriteComplete();

	public void onError(Exception e);

}
