/*
 * Creato il 25-giu-2007
 */
package it.seat.visualzoom.zoom.gui.bean;

/**
 * @author Leonardo landini
 */
public class VideoFormat {

	private String id;
	private String description;
	private int width;
	private int height;
	private float fps;

	/**
	 * @param id
	 * @param description
	 * @param width
	 * @param height
	 * @param fps
	 */
	public VideoFormat(String id, String description, int width, int height,
			float fps) {
		super();
		this.id = id;
		this.description = description;
		this.width = width;
		this.height = height;
		this.fps = fps;
	}

	public String getDescription() {
		return description;
	}

	public float getFps() {
		return fps;
	}

	public int getHeight() {
		return height;
	}

	public String getId() {
		return id;
	}

	public int getWidth() {
		return width;
	}

	public String toString() {
		return description;
	}
}
