package it.seat.visualzoom.player.layers;

import it.seat.visualzoom.player.SerialImageLoader;
import it.seat.visualzoom.zoom.data.AbstractImageLoader;
import it.seat.visualzoom.zoom.data.ImageLoaderListener;

import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class MapLayer extends SquareLayer implements ImageLoaderListener {
	private BufferedImage[] keyFrames;

	private Vector<Texture> textures;

	public MapLayer(int width, int height, int zoomLevels) {
		super(0, 0);

		this.width = width;
		this.height = height;
		keyFrames = new BufferedImage[zoomLevels];

		textures = new Vector<Texture>();
	}

	public Texture getTexture(int index) {
		return textures.get(index);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		for (BufferedImage keyFrame : keyFrames) {
			textures.add(TextureIO.newTexture(keyFrame, true));
		}
		setTexture(getTexture(0));// texture = textures.get(0);

		/* libera la memoria */
//		keyFrames = null;
//		System.gc();
	}

	/**
	 * Questo metodo serve solo in caso si voglia utilizzare il
	 * SerialImageLoader.
	 */
	public void initTextures(int width, int height, float lon, float lat,
			int zStart, int zEnd) {
		AbstractImageLoader imageLoader = new SerialImageLoader(lon, lat,
				width, height, zStart, zEnd);
		Vector<BufferedImage> keyFrames = imageLoader.loadImages();
		for (int i = 0; i < keyFrames.size(); i++) {
			onImageLoaded(keyFrames.size() - i - 1, keyFrames.get(i));
		}
	}

	/** + ImageLoaderListener + */
	@Override
	public void onError(int imageNumber, Exception e) {
	}

	@Override
	public void onImageLoaded(int imageNumber, int totImages) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onImageLoaded(int imageNumber, BufferedImage img) {
		keyFrames[keyFrames.length - imageNumber - 1] = img;
	}

	@Override
	public void onLoadingCancelled() {
		keyFrames = null;
		System.gc();
	}

	@Override
	public void onLoadingComplete() {
	}
	/** - ImageLoaderListener - */
	
	@Override
	public void destroy() {
		super.destroy();
		keyFrames = null;
		textures = null;
	}
}
