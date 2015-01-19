package it.seat.visualzoom.player;

import it.seat.visualzoom.player.layers.Layer;

import java.util.Vector;

import javax.media.opengl.GLAutoDrawable;

public class Movie extends LayeredScene {
	/** Attributi di questo oggetto. */
	private int width, height;

	private int frameRate;

	private Vector<MoviePart> movieParts;

	/** Attributi derivati. */
	private int length;

	/** Lo stato di questo oggetto. */
	private int currentTime;

	private int frameIndex;

	private MoviePart currentMoviePart;

	public Movie(int width, int height, int frameRate) {
		this.width = width;
		this.height = height;
		this.frameRate = frameRate;

		/* valori di default */
		movieParts = new Vector<MoviePart>();
	}

	public int getLength() {
		return length;
	}

	private int calculateLength() {
		int length = 0;
		for (MoviePart moviePart : movieParts) {
			length += moviePart.getLength();
		}
		return length;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public Vector<MoviePart> getMovieParts() {
		return movieParts;
	}

	public MoviePart getCurrentMoviePart() {
		return currentMoviePart;
	}

	public MoviePart findCurrentMoviePart() {
		int currentLength = 0;
		for (MoviePart moviePart : movieParts) {
			int length = moviePart.getLength();
			if (currentTime >= currentLength
					&& currentTime < currentLength + length) {
				return moviePart;
			}
			currentLength += length;
		}
		return null;
	}

	private int calculateRelativeTime() {
		int relativeTime = currentTime;
		for (MoviePart moviePart : movieParts) {
			if (moviePart == currentMoviePart) {
				return relativeTime;
			}
			relativeTime -= moviePart.getLength();
		}
		return relativeTime;
	}

	public void addMoviePart(MoviePart moviePart) {
		movieParts.add(moviePart);
		onMoviePartAdded(moviePart);
	}

	public void addMoviePart(int index, MoviePart moviePart) {
		movieParts.add(index, moviePart);
		onMoviePartAdded(moviePart);
	}

	public void removeMoviePart(MoviePart moviePart) {
		movieParts.remove(moviePart);
		onMoviePartRemoved(moviePart);
	}

	private void onMoviePartAdded(MoviePart moviePart) {
		length = calculateLength();
		currentMoviePart = moviePart;
	}

	private void onMoviePartRemoved(MoviePart moviePart) {
		length = calculateLength();
		int index = movieParts.indexOf(moviePart);
		if (index < movieParts.size() && index >= 0) {
			currentMoviePart = movieParts.get(index);
		} else if (movieParts.size() > 0) {
			currentMoviePart = movieParts.get(0);
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getAspectRatio() {
		return 1f * height / width;
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(int time) {
		currentTime = time;
		onTimeChanged();
	}

	public void updateCurrentTime(int increment) {
		currentTime += increment;
		onTimeChanged();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		for (MoviePart moviePart : movieParts) {
			moviePart.init(drawable);
		}

		super.init(drawable);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		currentMoviePart.display(drawable);

		super.display(drawable);
	}

	private void onTimeChanged() {
		/* corregge eventuali fuoriuscite dai limiti */
		if (currentTime < 0) {
			currentTime = 0;
		} else if (currentTime >= length) {
			currentTime = length - 1;
		}

		frameIndex = (int) Math.floor(currentTime / (1000f / frameRate));
		currentMoviePart = findCurrentMoviePart();
		int relativeTime = calculateRelativeTime();
		currentMoviePart.update(relativeTime);

		/* aggiorna i layer */
		for (Layer layer : layers) {
			layer.update(currentTime, length);
		}
	}

	public int getCurrentFrameIndex() {
		return frameIndex;
	}

	public void goToFrame(int frame) {
		currentTime = (int) (frame * 1000f / frameRate);
		onTimeChanged();

	}

	public String toString() {
		return "[currentTime: " + getCurrentTime() + ", currentFrameIndex: "
				+ getCurrentFrameIndex() + ", length: " + getLength()
				+ ", currentMoviePart:\n" + getCurrentMoviePart() + "\n]";
	}

	/**
	 * Metodo main di test.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int width = 1440;
		int height = 1080;
		int frameRate = 25;
		Movie movie = new Movie(width, height, frameRate);

		MoviePart moviePart1 = new MoviePart(5000);
		movie.addMoviePart(moviePart1);

		MoviePart moviePart2 = new MoviePart(5000);
		movie.addMoviePart(moviePart2);

		System.out.println(movie);

		/*
		 * questo metodo accetta una direzione e una velocità moltiplicate
		 * assieme
		 */
		movie.updateCurrentTime(1 * 1);
		System.out.println(movie);

		/* quest'altro invece prende il tempo in millisecondi */
		movie.setCurrentTime(5001);
		System.out.println(movie);

		movie.removeMoviePart(moviePart2);

		System.out.println(movie);
	}

	public void destroy() {
		for (MoviePart moviePart : movieParts) {
			moviePart.destroy();
			moviePart = null;
		}
	}
}
