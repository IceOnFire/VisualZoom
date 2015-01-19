package it.seat.visualzoom.zoom.video;

import it.seat.visualzoom.player.MoviePlayer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.media.*;
import javax.media.control.*;
import javax.media.protocol.*;
import javax.media.datasink.*;
import javax.media.format.VideoFormat;

/**
 * This program takes a list of JPEG image files and convert them into a
 * QuickTime movie.
 */
public class JpegImagesToMovie extends Thread implements ControllerListener, DataSinkListener {
	private MoviePlayer player;
	private File file;
	private Vector<MovieWriterListener> listeners;

	public JpegImagesToMovie(MoviePlayer player, File file) {
		this.player = player;
		this.file = file;
		listeners = new Vector<MovieWriterListener>();
	}

	public void addListener(MovieWriterListener listener) {
		listeners.add(listener);
	}

	@Override
	public void run() {
		ImageDataSource ids = new ImageDataSource(player);
		for (MovieWriterListener listener : listeners) {
			ids.addListener(listener);
		}

		URL selFileURL = null;
		try {
			selFileURL = file.toURL();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		MediaLocator oml;
		if ((oml = new MediaLocator(selFileURL)) == null) {
			System.err.println("Cannot build media locator from: "
					+ selFileURL);
		}

		Processor p;
		try {
			System.err
					.println("- create processor for the image datasource ...");
			p = Manager.createProcessor(ids);
		} catch (Exception e) {
			System.err
					.println("Yikes!  Cannot create a processor from the data source.");
			return;
		}

		p.addControllerListener(this);

		// Put the Processor into configured state so we can set
		// some processing options on the processor.
		p.configure();
		if (!waitForState(p, Processor.Configured)) {
			System.err.println("Failed to configure the processor.");
			return;
		}

		// Set the output content descriptor to QuickTime.
		p.setContentDescriptor(new ContentDescriptor(
				FileTypeDescriptor.QUICKTIME));

		// Query for the processor for supported formats.
		// Then set it on the processor.
		TrackControl tcs[] = p.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
			System.err.println("The mux does not support the input format: "
					+ tcs[0].getFormat());
			return;
		}

		tcs[0].setFormat(f[0]);

		System.err.println("Setting the track format to: " + f[0]);

		// We are done with programming the processor. Let's just
		// realize it.
		p.realize();
		if (!waitForState(p, Processor.Realized)) {
			System.err.println("Failed to realize the processor.");
			return;
		}

		// Now, we'll need to create a DataSink.
		DataSink dsink;
		if ((dsink = createDataSink(p, oml)) == null) {
			System.err
					.println("Failed to create a DataSink for the given output MediaLocator: "
							+ oml);
			return;
		}

		dsink.addDataSinkListener(this);
		fileDone = false;

		System.err.println("start processing...");

		// OK, we can now start the actual transcoding.
		try {
			p.start();
			dsink.start();
		} catch (IOException e) {
			System.err.println("IO error during processing");
			return;
		}

		// Wait for EndOfStream event.
		waitForFileDone();

		// Cleanup.
		try {
			dsink.close();
		} catch (Exception e) {
		}
		p.removeControllerListener(this);

		System.err.println("...done processing.");
		player.setCaptureMode(false);
	}

	public boolean doIt(MoviePlayer player, File selFile) {
		ImageDataSource ids = new ImageDataSource(player);

		URI selFileURI = selFile.toURI();
		URL selFileURL = null;
		try {
			selFileURL = selFileURI.toURL();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MediaLocator oml;
		if ((oml = new MediaLocator(selFileURL)) == null) {
			System.err.println("Cannot build media locator from: "
					+ selFileURL);
		}

		Processor p;

		try {
			System.err
					.println("- create processor for the image datasource ...");
			p = Manager.createProcessor(ids);
		} catch (Exception e) {
			System.err
					.println("Yikes!  Cannot create a processor from the data source.");
			return false;
		}

		p.addControllerListener(this);

		// Put the Processor into configured state so we can set
		// some processing options on the processor.
		p.configure();
		if (!waitForState(p, Processor.Configured)) {
			System.err.println("Failed to configure the processor.");
			return false;
		}

		// Set the output content descriptor to QuickTime.
		p.setContentDescriptor(new ContentDescriptor(
				FileTypeDescriptor.QUICKTIME));

		// Query for the processor for supported formats.
		// Then set it on the processor.
		TrackControl tcs[] = p.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
			System.err.println("The mux does not support the input format: "
					+ tcs[0].getFormat());
			return false;
		}

		tcs[0].setFormat(f[0]);

		System.err.println("Setting the track format to: " + f[0]);

		// We are done with programming the processor. Let's just
		// realize it.
		p.realize();
		if (!waitForState(p, Processor.Realized)) {
			System.err.println("Failed to realize the processor.");
			return false;
		}

		// Now, we'll need to create a DataSink.
		DataSink dsink;
		if ((dsink = createDataSink(p, oml)) == null) {
			System.err
					.println("Failed to create a DataSink for the given output MediaLocator: "
							+ oml);
			return false;
		}

		dsink.addDataSinkListener(this);
		fileDone = false;

		System.err.println("start processing...");

		// OK, we can now start the actual transcoding.
		try {
			p.start();
			dsink.start();
		} catch (IOException e) {
			System.err.println("IO error during processing");
			return false;
		}

		// Wait for EndOfStream event.
		waitForFileDone();

		// Cleanup.
		try {
			dsink.close();
		} catch (Exception e) {
		}
		p.removeControllerListener(this);

		System.err.println("...done processing.");

		return true;
	}

	/**
	 * Create the DataSink.
	 */
	DataSink createDataSink(Processor p, MediaLocator outML) {

		DataSource ds;

		if ((ds = p.getDataOutput()) == null) {
			System.err
					.println("Something is really wrong: the processor does not have an output DataSource");
			return null;
		}

		DataSink dsink;

		try {
			System.err.println("- create DataSink for: " + outML);
			dsink = Manager.createDataSink(ds, outML);
			dsink.open();
		} catch (Exception e) {
			System.err.println("Cannot create the DataSink: " + e);
			return null;
		}

		return dsink;
	}

	Object waitSync = new Object();

	boolean stateTransitionOK = true;

	/**
	 * Block until the processor has transitioned to the given state. Return
	 * false if the transition failed.
	 */
	boolean waitForState(Processor p, int state) {
		synchronized (waitSync) {
			try {
				while (p.getState() < state && stateTransitionOK)
					waitSync.wait();
			} catch (Exception e) {
			}
		}
		return stateTransitionOK;
	}

	/**
	 * Controller Listener.
	 */
	public void controllerUpdate(ControllerEvent evt) {

		if (evt instanceof ConfigureCompleteEvent
				|| evt instanceof RealizeCompleteEvent
				|| evt instanceof PrefetchCompleteEvent) {
			synchronized (waitSync) {
				stateTransitionOK = true;
				waitSync.notifyAll();
			}
		} else if (evt instanceof ResourceUnavailableEvent) {
			synchronized (waitSync) {
				stateTransitionOK = false;
				waitSync.notifyAll();
			}
		} else if (evt instanceof EndOfMediaEvent) {
			evt.getSourceController().stop();
			evt.getSourceController().close();
		}
	}

	Object waitFileSync = new Object();

	boolean fileDone = false;

	boolean fileSuccess = true;

	/**
	 * Block until file writing is done.
	 */
	boolean waitForFileDone() {
		synchronized (waitFileSync) {
			try {
				while (!fileDone)
					waitFileSync.wait();
			} catch (Exception e) {
			}
		}
		return fileSuccess;
	}

	/**
	 * Event handler for the file writer.
	 */
	public void dataSinkUpdate(DataSinkEvent evt) {

		if (evt instanceof EndOfStreamEvent) {
			synchronized (waitFileSync) {
				fileDone = true;
				waitFileSync.notifyAll();
			}
		} else if (evt instanceof DataSinkErrorEvent) {
			synchronized (waitFileSync) {
				fileDone = true;
				fileSuccess = false;
				waitFileSync.notifyAll();
			}
		}
	}

	static void prUsage() {
		System.err
				.println("Usage: java JpegImagesToMovie -w <width> -h <height> -f <frame rate> -o <output URL> <input JPEG file 1> <input JPEG file 2> ...");
		System.exit(-1);
	}

	/**
	 * Create a media locator from the given string.
	 */
	static MediaLocator createMediaLocator(String url) {

		MediaLocator ml;

		if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null)
			return ml;

		if (url.startsWith(File.separator)) {
			if ((ml = new MediaLocator("file:" + url)) != null)
				return ml;
		} else {
			String file = "file:" + System.getProperty("user.dir")
					+ File.separator + url;
			if ((ml = new MediaLocator(file)) != null)
				return ml;
		}

		return null;
	}

	// /////////////////////////////////////////////
	//
	// Inner classes.
	// /////////////////////////////////////////////

	/**
	 * A DataSource to read from a list of JPEG image files and turn that into a
	 * stream of JMF buffers. The DataSource is not seekable or positionable.
	 */
	class ImageDataSource extends PullBufferDataSource {

		ImageSourceStream streams[];

		ImageDataSource(MoviePlayer player) {
			streams = new ImageSourceStream[1];
			streams[0] = new ImageSourceStream(player);
		}

		public void addListener(MovieWriterListener listener) {
			streams[0].addListener(listener);
		}

		public void setLocator(MediaLocator source) {
		}

		public MediaLocator getLocator() {
			return null;
		}

		/**
		 * Content type is of RAW since we are sending buffers of video frames
		 * without a container format.
		 */
		public String getContentType() {
			return ContentDescriptor.RAW;
		}

		public void connect() {
		}

		public void disconnect() {
		}

		public void start() {
		}

		public void stop() {
		}

		/**
		 * Return the ImageSourceStreams.
		 */
		public PullBufferStream[] getStreams() {
			return streams;
		}

		/**
		 * We could have derived the duration from the number of frames and
		 * frame rate. But for the purpose of this program, it's not necessary.
		 */
		public Time getDuration() {
			return DURATION_UNKNOWN;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public Object getControl(String type) {
			return null;
		}
	}

	/**
	 * The source stream to go along with ImageDataSource.
	 */
	class ImageSourceStream implements PullBufferStream {
		private int framesNumber;
//		private Vector<BufferedImage> images;
		private VideoFormat format;
		Vector<MovieWriterListener> listeners;
		private int nextFrame; // index of the next image to be read.
		private boolean ended;

		/**
		 * Avrei voluto tanto creare uno stream che prende le immagini dalla scena
		 * dinamicamente, ma a quanto pare non è possibile su Windows. Pertanto
		 * questo stream memorizza in un vettore tutti i frame che compongono il
		 * filmato, dopodiché li mette tutti insieme nel buffer.
		 * @param player
		 */
		public ImageSourceStream(MoviePlayer player) {
			int width = player.getWidth();
			int height = player.getHeight();
			int length = player.getMovie().getLength();
			int frameRate = player.getMovie().getFrameRate();
			framesNumber = (int) (length * frameRate / 1000f);
			player.setCaptureMode(true);
			/* riavvolge il nastro: serve a evitare immagini sbagliate al primo frame */
			player.goToFrame(0);
//			images = new Vector<BufferedImage>();
//			for (int frameIndex=0; frameIndex<framesNumber; frameIndex++) {
//				player.goToFrame(frameIndex);
//				images.add(player.getScreenshot());
//			}
			
			format = new VideoFormat(VideoFormat.JPEG, new Dimension(width,
					height), Format.NOT_SPECIFIED, Format.byteArray,
					(float) frameRate);
			listeners = new Vector<MovieWriterListener>();
		}

		/**
		 * We should never need to block assuming data are read from files.
		 */
		public boolean willReadBlock() {
			return false;
		}

		/**
		 * This is called from the Processor to read a frame worth of video
		 * data.
		 */
		public void read(Buffer buf) throws IOException {
			// Check if we've finished all the frames.
			if (nextFrame >= framesNumber) {
				// We are done. Set EndOfMedia.
				System.err.println("Done reading all images.");
				buf.setEOM(true);
				buf.setOffset(0);
				buf.setLength(0);
				ended = true;
				sendWriteComplete();
				return;
			}

			sendProgress(100 * nextFrame / framesNumber);
			/* non so perché ma non funziona su Windows!!! */
			player.goToFrame(nextFrame);
			BufferedImage screenshot = player.getScreenshot();
//			BufferedImage screenshot = images.elementAt(nextFrame);
			if (screenshot != null) {
				ByteArrayOutputStream bas = new ByteArrayOutputStream();
				try {
//					ImageIO.write(screenshot, "jpg", new File("screenshot"
//							+ nextFrame + ".jpg"));
					ImageIO.write(screenshot, "jpg", bas);
				} catch (IOException e) {
					sendError(e);
					throw e;
				}
				byte[] data = bas.toByteArray();
				buf.setData(data);
				buf.setOffset(0);
				buf.setLength(data.length);
				buf.setFormat(format);
				buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
			}
			nextFrame++;
		}

		/**
		 * Return the format of each video frame. That will be JPEG.
		 */
		public Format getFormat() {
			return format;
		}

		public ContentDescriptor getContentDescriptor() {
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		public long getContentLength() {
			return 0;
		}

		public boolean endOfStream() {
			return ended;
		}

		public Object[] getControls() {
			return new Object[0];
		}

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
}
