package it.seat.visualzoom.zoom.video;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;

/**
 * The source stream to go along with ImageDataSource.
 */
abstract class AbstractImageSourceStream implements PullBufferStream {
	Vector<MovieWriterListener> listeners;
	VideoFormat format;
	int nextFrame; // index of the next image to be read.
	boolean ended;

	public AbstractImageSourceStream(int width, int height, float frameRate) {
		listeners = new Vector<MovieWriterListener>();
		format = new VideoFormat(VideoFormat.JPEG,
				new Dimension(width, height), Format.NOT_SPECIFIED,
				Format.byteArray, frameRate);
		nextFrame = 0;
		ended = false;
	}

	@Override
	public boolean willReadBlock() {
		return false;
	}

	@Override
	public abstract void read(Buffer buf) throws IOException;

	@Override
	public Format getFormat() {
		return format;
	}

	@Override
	public ContentDescriptor getContentDescriptor() {
		return new ContentDescriptor(ContentDescriptor.RAW);
	}

	@Override
	public long getContentLength() {
		return 0;
	}

	@Override
	public boolean endOfStream() {
		return ended;
	}

	@Override
	public Object[] getControls() {
		return new Object[0];
	}

	@Override
	public Object getControl(String type) {
		return null;
	}

	public void addListener(MovieWriterListener listener) {
		listeners.add(listener);
	}

	protected void sendProgress(int perc) {
		for (MovieWriterListener listener : listeners) {
			listener.onProgress(perc);
		}
	}

	protected void sendWriteComplete() {
		for (MovieWriterListener listener : listeners) {
			listener.onWriteComplete();
		}
	}

	protected void sendError(Exception e) {
		for (MovieWriterListener listener : listeners) {
			listener.onError(e);
		}
	}
}