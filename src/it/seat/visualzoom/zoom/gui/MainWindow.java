/*
 * Creato il 8-mag-2007
 */
package it.seat.visualzoom.zoom.gui;

import it.seat.visualzoom.logger.Log;
import it.seat.visualzoom.player.Movie;
import it.seat.visualzoom.player.MoviePlayer;
import it.seat.visualzoom.zoom.data.AbstractImageLoader;
import it.seat.visualzoom.zoom.data.ImageLoaderListener;
import it.seat.visualzoom.zoom.geocoding.Location;
import it.seat.visualzoom.zoom.gui.actions.CloseMovieAction;
import it.seat.visualzoom.zoom.gui.actions.NewMovieAction;
import it.seat.visualzoom.zoom.gui.actions.OpenProxyDialogAction;
import it.seat.visualzoom.zoom.gui.actions.QuitAction;
import it.seat.visualzoom.zoom.gui.actions.SaveMovieAction;
import it.seat.visualzoom.zoom.gui.actions.ShowPreviewAction;
import it.seat.visualzoom.zoom.gui.event.NewProjectDialogListener;
import it.seat.visualzoom.zoom.gui.event.ProgressDialogListener;
import it.seat.visualzoom.zoom.video.MovieWriter;
import it.seat.visualzoom.zoom.video.MovieWriterListener;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

/**
 * @author Leonardo Landini
 */
public class MainWindow extends JFrame implements ImageLoaderListener,
		ProgressDialogListener, NewProjectDialogListener, MovieWriterListener {
	private static final long serialVersionUID = 1L;

	private static MainWindow singleton;

	private static final int STATUS_WAITING = 0;

	private static final int STATUS_LOADING = 1;

	private static final int STATUS_PREVIEW = 2;

	private static final int STATUS_SAVING = 3;

	private static final int DEFAULT_WIDTH = 640;

	private static final int DEFAULT_HEIGHT = 480;

	private static final int DEFAULT_FPS = 25;

	private JDesktopPane desktopPane;

	private MoviePreview preview;

	// private LayerWindow titleWindow;

	/* Actions */
	private AbstractAction newMovieAction;

	private AbstractAction previewAction;

	private AbstractAction saveMovieAction;

	private AbstractAction closeMovieAction;

	private AbstractAction quitAction;

	private AbstractAction openProxyDialogAction;

	// private AbstractAction addTitleAction;
	// private AbstractAction showTitleWindowAction;

	// Application objects
	// SearchData searchData;
	private Movie movie;

	private int movieLength;

	private int movieWidth;

	private int movieHeight;

	// min 4, max 16
	private int zStart;

	private int zEnd;

	private int framerate;

	// Location locationSelFromList;
	private Location currentLocation;

	// private BufferedImage[] keyFrames;
	private ProgressDialog progress;

	private int status;

	private AbstractImageLoader imageLoader;

	private MovieWriter movieWriter;

	// boolean saveOk = true;

	public static MainWindow getInstance() {
		if (singleton == null) {
			singleton = new MainWindow();
		}
		return singleton;
	}

	/**
	 * This is the default constructor
	 */
	public MainWindow() {
		super();
		newMovieAction = new NewMovieAction();
		previewAction = new ShowPreviewAction();
		saveMovieAction = new SaveMovieAction();
		closeMovieAction = new CloseMovieAction();
		quitAction = new QuitAction();
		openProxyDialogAction = new OpenProxyDialogAction();
		// addTitleAction = new AddTitleAction();
		// showTitleWindowAction = new ShowTitleWindowAction();

		setWaitingMode();
		movieWidth = DEFAULT_WIDTH;
		movieHeight = DEFAULT_HEIGHT;
		framerate = DEFAULT_FPS;

		/* menu bar */
		JMenuBar mainMenuBar = new JMenuBar();

		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");
		JMenuItem newItem = new JMenuItem(newMovieAction);
		JMenuItem closeItem = new JMenuItem(closeMovieAction);
		JMenuItem saveItem = new JMenuItem(saveMovieAction);
		JMenuItem quitItem = new JMenuItem(quitAction);
		newItem.setText("Nuovo filmato...");
		closeItem.setText("Chiudi");
		saveItem.setText("Salva...");
		quitItem.setText("Esci");
		fileMenu.add(newItem);
		fileMenu.add(closeItem);
		fileMenu.add(saveItem);
		fileMenu.add(quitItem);
		mainMenuBar.add(fileMenu);

		JMenu prefsMenu = new JMenu();
		prefsMenu.setText("Preferenze");
		JMenuItem connectionItem = new JMenuItem(openProxyDialogAction);
		connectionItem.setText("Connessione...");
		prefsMenu.add(connectionItem);
		mainMenuBar.add(prefsMenu);

		setJMenuBar(mainMenuBar);

		/* tool bar */
		setLayout(new BorderLayout());
		JToolBar toolBar = new JToolBar();
		JButton newButton = new JButton(newMovieAction);
		JButton previewButton = new JButton(previewAction);
		JButton saveButton = new JButton(saveMovieAction);
		// JButton titleButton = new JButton(addTitleAction);
		newButton.setIcon(new ImageIcon("icons/new.png"));
		previewButton.setIcon(new ImageIcon("icons/preview.png"));
		saveButton.setIcon(new ImageIcon("icons/save.png"));
		// titleButton.setText("Aggiungi titoli...");
		toolBar.add(newButton);
		toolBar.add(previewButton);
		toolBar.add(saveButton);
		// toolBar.add(titleButton);
		add(toolBar, BorderLayout.NORTH);

		/* desktop pane */
		desktopPane = new JDesktopPane();
		desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		/* layers window */
		// LayerWindow titleWindow = new LayerWindow(layers);
		// titleWindow.setVisible(false);
		// desktopPane.add(titleWindow);
		add(desktopPane, BorderLayout.CENTER);

		/* status bar */
		JPanel statusBar = new JPanel();
		add(statusBar, BorderLayout.SOUTH);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void setImageLoader(AbstractImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	/*
	 * Metodi di cambio stato
	 */
	private void setWaitingMode() {
		status = STATUS_WAITING;
		newMovieAction.setEnabled(true);
		previewAction.setEnabled(false);
		saveMovieAction.setEnabled(false);
		closeMovieAction.setEnabled(false);
	}

	public void setLoadingMode() {
		status = STATUS_LOADING;
		newMovieAction.setEnabled(false);
		previewAction.setEnabled(false);
		saveMovieAction.setEnabled(false);
		closeMovieAction.setEnabled(false);
	}

	public void setPreviewMode() {
		status = STATUS_PREVIEW;
		newMovieAction.setEnabled(true);
		previewAction.setEnabled(true);
		saveMovieAction.setEnabled(true);
		closeMovieAction.setEnabled(true);
	}

	public void setSavingMode() {
		status = STATUS_SAVING;
		// saveOk = true;
		newMovieAction.setEnabled(false);
		previewAction.setEnabled(false);
		saveMovieAction.setEnabled(false);
		closeMovieAction.setEnabled(false);
	}

	public void closeMovie() {
		if (preview != null/* && preview.isVisible()*/) {
			int choice = JOptionPane.showConfirmDialog(MainWindow.this,
					"Chiudere il precedente progetto?", "",
					JOptionPane.OK_CANCEL_OPTION);
			if (choice == JOptionPane.OK_OPTION) {
				preview.dispose();
				currentLocation = null;
				movie = null;
				destroyPreview();
				setWaitingMode();
			}
		}
	}

	/* distrugge a cascata tutta la preview */
	private void destroyPreview() {
		preview.destroy();
		preview = null;
		System.gc();
	}

	public void showPreview(Movie movie, int movieWidth, int movieHeight) {
		MoviePlayer player = new MoviePlayer(movie, movieWidth, movieHeight);
		preview = new MoviePreview(player);
		preview.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		desktopPane.add(preview);
		setPreviewMode();
	}

	/*
	 * Metodi di ascolto
	 * 
	 */
	/** + MovieWriterListener + */
	@Override
	public void onError(Exception e) {
		Log.getLogger().warning(e.toString());
	}

	@Override
	public void onProgress(int perc) {
		// do nothing
	}

	@Override
	public void onWriteComplete() {
		setPreviewMode();
		JOptionPane.showMessageDialog(MainWindow.this,
				"Salvataggio completato.");
	}

	/** - MovieWriterListener - */

	/** + ProgressDialogListener + */
	@Override
	public void operationCancelled() {
		if (status == STATUS_LOADING) {
			setWaitingMode();
			if (imageLoader != null)
				imageLoader.stopLoading();
		} else if (status == STATUS_SAVING) {
			setPreviewMode();
			if (movieWriter != null)
				movieWriter.stopWriting();
		}
	}

	/** - ProgressDialogListener - */

	/** + NewProjectDialogListener + */
	@Override
	public void onNewProjectConfirm(Location location, int width, int height,
			int zStart, int zEnd, int length) {
		currentLocation = location;
		movieWidth = width;
		movieHeight = height;
		this.zStart = zStart;
		this.zEnd = zEnd;
		movieLength = length;
	}

	@Override
	public void onNewProjectCancel() {
		currentLocation = null;
		// nothing to do
	}

	/** - NewProjectDialogListener - */

	public int getStatus() {
		return status;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public int getMovieWidth() {
		return movieWidth;
	}

	public int getMovieHeight() {
		return movieHeight;
	}

	public int getMovieLength() {
		return movieLength;
	}

	public int getZStart() {
		return zStart;
	}

	public int getZEnd() {
		return zEnd;
	}

	public int getFramerate() {
		return framerate;
	}

	public MoviePreview getPreview() {
		return preview;
	}

	public MovieWriter getMovieWriter() {
		return movieWriter;
	}

	public void setMovieWriter(MovieWriter movieWriter) {
		this.movieWriter = movieWriter;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public void setProgress(ProgressDialog progress) {
		this.progress = progress;
	}

	/** + ImageLoaderListener + */
	@Override
	public void onError(int imageNumber, Exception e) {
		Log.getLogger().warning(e.toString());
	}

	@Override
	public void onImageLoaded(int imageNumber, int totImages) {
		if (progress != null && progress.isShowing()) {
			int prog = progress.getProgress();
			prog += 100 / totImages;
			progress.setProgress(prog);
		}
	}

	@Override
	public void onImageLoaded(int imageNumber, BufferedImage img) {
	}

	@Override
	public void onLoadingCancelled() {
		Log.getLogger().finer("Loading cancelled");
		if (progress != null && this.progress.isShowing()) {
			this.progress.dispose();
		}
	}

	@Override
	public void onLoadingComplete() {
		if (progress != null && progress.isShowing()) {
			progress.dispose();
		}
		showPreview(movie, movieWidth, movieHeight);
	}
	/** - ImageLoaderListener - */
}
