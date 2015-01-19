package it.seat.visualzoom.zoom.video;

import it.seat.visualzoom.zoom.gui.bean.Title;

import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.media.Buffer;

/**
 * The source stream to go along with ImageDataSource.
 */
class ImageSourceStream extends AbstractImageSourceStream {
	FrameGenerator fg;
	RenderingHints hints;

	public ImageSourceStream(int width, int height, float frameRate,
			float length, BufferedImage[] keyFrames, BufferedImage logo,
			ArrayList<Title> titles) {
		super(width, height, frameRate);

		fg = new FrameGenerator(keyFrames, logo, width, height, frameRate,
				length, titles);
		fg.realize();

		HashMap<Key, Object> hintMap = new HashMap<Key, Object>();
		hintMap.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		hintMap.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hintMap.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hintMap.put(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		hintMap.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		hintMap.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		this.hints = new RenderingHints(hintMap);
	}

	@Override
	public void read(Buffer buf) throws IOException {
		// Check if we've finished all the frames.
		if (nextFrame >= fg.getNumFrames()) {
			// We are done. Set EndOfMedia.
			System.err.println("Done reading all images. " + nextFrame
					+ " total.");
			buf.setEOM(true);
			buf.setOffset(0);
			buf.setLength(0);
			ended = true;
			sendWriteComplete();
			return;
		}
		sendProgress(100 * nextFrame / fg.getNumFrames());
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		if (fg.frames[nextFrame] != null) {
			try {
				ImageIO.write(fg.getFrame(nextFrame, hints), "jpg", bas);
			} catch (IOException e) {
				sendError(e);
				throw e;
			}
		}
		byte[] data = bas.toByteArray();
		buf.setData(data);
		buf.setOffset(0);
		buf.setLength(data.length);
		buf.setFormat(format);
		buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
		nextFrame++;
	}
}