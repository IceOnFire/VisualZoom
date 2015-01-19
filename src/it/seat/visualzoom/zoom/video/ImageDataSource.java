package it.seat.visualzoom.zoom.video;

import it.seat.visualzoom.player.MoviePlayer;
import it.seat.visualzoom.zoom.gui.bean.Title;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

class ImageDataSource extends PullBufferDataSource {
	AbstractImageSourceStream[] streams;

	ImageDataSource(MoviePlayer player) {
		streams = new GLImageSourceStream[1];
		streams[0] = new GLImageSourceStream(player);
	}

	ImageDataSource(int width, int height, float frameRate, float length,
			BufferedImage[] keyFrames, BufferedImage logo,
			ArrayList<Title> titles) {

		streams = new GLImageSourceStream[1];
		streams[0] = new ImageSourceStream(width, height, frameRate, length,
				keyFrames, logo, titles);
	}

	public void addListener(MovieWriterListener listener) {
		streams[0].addListener(listener);
	}

	@Override
	public void setLocator(MediaLocator source) {
	}

	@Override
	public MediaLocator getLocator() {
		return null;
	}

	/**
	 * Content type is of RAW since we are sending buffers of video frames
	 * without a container format.
	 */
	@Override
	public String getContentType() {
		return ContentDescriptor.RAW;
	}

	@Override
	public void connect() {
	}

	@Override
	public void disconnect() {
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	/**
	 * Return the ImageSourceStreams.
	 */
	@Override
	public PullBufferStream[] getStreams() {
		return streams;
	}

	/**
	 * We could have derived the duration from the number of frames and frame
	 * rate. But for the purpose of this program, it's not necessary.
	 */
	@Override
	public Time getDuration() {
		return DURATION_UNKNOWN;
	}

	@Override
	public Object[] getControls() {
		return streams[0].getControls();
	}

	@Override
	public Object getControl(String type) {
		return streams[0].getControl(type);
	}
}
