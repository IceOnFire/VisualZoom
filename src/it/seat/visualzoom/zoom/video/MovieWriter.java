/*
 * Creato il 11-apr-2007
 *
 */
package it.seat.visualzoom.zoom.video;

import it.seat.visualzoom.logger.Log;
import it.seat.visualzoom.player.MoviePlayer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Vector;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoProcessorException;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.FileTypeDescriptor;

/**
 * @author Leo
 * 
 */
public class MovieWriter extends Thread implements ControllerListener,
		DataSinkListener {

	private Vector<MovieWriterListener> listeners;
	private MoviePlayer player;
	private File file;
	// control
	boolean realized = false;
	boolean configured = false;
	boolean eom = false;
	boolean eos = false;
	boolean stop = false;

	public MovieWriter(MoviePlayer player, File file) {
		super();
		this.player = player;
		this.file = file;
		listeners = new Vector<MovieWriterListener>();
	}

	public void addListener(MovieWriterListener listener) {
		listeners.add(listener);
	}

	public void stopWriting() {
		stop = true;
	}

	public void run() {
		try {
			this.stop = false;
			URI selFileURI = file.toURI();
			URL selFileURL = selFileURI.toURL();
			MediaLocator oml;
			if ((oml = new MediaLocator(selFileURL)) == null) {
				System.err.println("Cannot build media locator from: "
						+ selFileURL);
			}
			player.setCaptureMode(true);
			ImageDataSource ids = new ImageDataSource(player);
			for (MovieWriterListener listener : listeners) {
				ids.addListener(listener);
			}
			Processor p = Manager.createProcessor(ids);
			p.addControllerListener(this);
			p.configure();
			while (!configured) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
			p.setContentDescriptor(new ContentDescriptor(
					FileTypeDescriptor.QUICKTIME));
			TrackControl tc = p.getTrackControls()[0];
			Format f[] = tc.getSupportedFormats();
			if (f.length > 0)
				tc.setFormat(f[0]);
			p.realize();
			while (!realized) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
			System.err.println("Creating data sink for: " + selFileURL);
			DataSink dsink = Manager.createDataSink(p.getDataOutput(), oml);
			dsink.addDataSinkListener(this);

			for (int i = 0; i < dsink.getControls().length; i++) {
				System.err.println(dsink.getControls()[i].getClass()
						.getSuperclass());
			}
			dsink.open();
			System.err.println("Start processor...");
			p.start();
			System.err.println("Start data sink...");
			dsink.start();
			// MovieGenerator imageToMovie = new MovieGenerator();
			// long start = System.currentTimeMillis();
			// imageToMovie.doIt(width, height, fps, length, images, logo, oml);
			// long end = System.currentTimeMillis();
			// Log.getLogger().finer("Done in "+((float)(end-start)/1000)+"
			// s.");
			// while(!eom){
			// try {
			// sleep(10);
			// } catch (InterruptedException e2) {
			// e2.printStackTrace();
			// }
			// }
			// p.close();
			// p.removeControllerListener(this);
			// System.err.println("Processor closed.");
			while (!eos) {
				try {
					Thread.sleep(10);
					if (stop) {
						dsink.stop();
						break;
					}
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}
			}
			dsink.close();
			System.err.println("Data Sink closed.");
			if (!stop)
				System.err.println("Done processing.");
			else {
				if (this.file.exists())
					this.file.delete();
				System.err.println("Processing cancelled.");
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (NoProcessorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoDataSinkException e) {
			e.printStackTrace();
		} finally {
			player.setCaptureMode(false);
		}
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see javax.media.ControllerListener#controllerUpdate(javax.media.ControllerEvent)
	 */
	public void controllerUpdate(ControllerEvent e) {
		Log.getLogger().finer("ControllerEvent: " + e.toString());
		if (e instanceof ConfigureCompleteEvent)
			configured = true;
		if (e instanceof RealizeCompleteEvent)
			realized = true;
		if (e instanceof EndOfMediaEvent) {
			eom = true;
			e.getSourceController().stop();
			e.getSourceController().close();
		}
	}

	/*
	 * (non Javadoc)
	 * 
	 * @see javax.media.datasink.DataSinkListener#dataSinkUpdate(javax.media.datasink.DataSinkEvent)
	 */
	public void dataSinkUpdate(DataSinkEvent e) {
		Log.getLogger().finer("DataSinkEvent: " + e.toString());
		if (e instanceof EndOfStreamEvent)
			eos = true;
	}

	public static void main(String[] args) throws NoProcessorException,
			IOException {
		ImageDataSource ids = new ImageDataSource(720, 576, 25, 10,
				new BufferedImage[1], null, null);
		Processor p = Manager.createProcessor(ids);
		p.configure();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		p
				.setContentDescriptor(new ContentDescriptor(
						FileTypeDescriptor.MSVIDEO));
		TrackControl tc = p.getTrackControls()[0];
		Format f[] = tc.getSupportedFormats();
		for (int i = 0; i < f.length; i++) {
			System.out.println(f[i]);
		}
		tc.setFormat(f[0]);
		p.realize();
		System.out.println("Fatto.");
		System.exit(0);
	}

}
