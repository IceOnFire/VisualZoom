package it.seat.visualzoom.player;

import it.seat.visualzoom.player.layers.Layer;

public class MoviePart extends LayeredScene {
	private int length;

	/** Lo stato di questo oggetto. */
	private int currentTime;

	public MoviePart(int length) {
		this.length = length;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}

	public int getLength() {
		return length;
	}
	
	public void update(int time) {
		this.currentTime = time;
		
		/* aggiorna i layer */
		for (Layer layer : layers) {
			layer.update(currentTime, length);
		}
	}
	
	public String toString() {
		return "currentTime: " + currentTime;
	}

	public void destroy() {
		for (Layer layer : layers) {
			layer.destroy();
			layer = null;
		}
	}
}
