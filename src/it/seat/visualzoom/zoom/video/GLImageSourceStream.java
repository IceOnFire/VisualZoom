package it.seat.visualzoom.zoom.video;

import it.seat.visualzoom.player.MoviePlayer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.Buffer;

public class GLImageSourceStream extends AbstractImageSourceStream {
	private MoviePlayer player;

	public GLImageSourceStream(MoviePlayer player) {
		super(player.getMovie().getWidth(), player.getMovie().getHeight(), player.getMovie().getFrameRate());

		this.player = player;
	}

	@Override
	public void read(Buffer buf) throws IOException {
		int framesNumber = (int) (player.getMovie().getLength() * player.getMovie().getFrameRate() / 1000f);
		// Check if we've finished all the frames.
		if (nextFrame >= framesNumber) {
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
		sendProgress(100 * nextFrame / framesNumber);
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		player.goToFrame(nextFrame);
		BufferedImage screenshot = player.getScreenshot();
		if (screenshot != null) {
			try {
//				ImageIO.write(screenshot, "jpg", new File("screenshot"+nextFrame+".jpg"));
				ImageIO.write(screenshot, "jpg", bas);
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
