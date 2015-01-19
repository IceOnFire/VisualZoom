/*
 * Creato il 10-apr-2007
 */
package it.seat.visualzoom.zoom.gui;

import it.seat.visualzoom.player.IMoviePlayer;
import it.seat.visualzoom.player.Movie;
import it.seat.visualzoom.player.MoviePart;
import it.seat.visualzoom.player.MoviePlayer;
import it.seat.visualzoom.player.SerialImageLoader;
import it.seat.visualzoom.player.effects.CyclicScaleEffect;
import it.seat.visualzoom.player.layers.FixedLayer;
import it.seat.visualzoom.player.layers.Layer;
import it.seat.visualzoom.player.layers.MapLayer;
import it.seat.visualzoom.zoom.data.AbstractImageLoader;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * @author Leonardo Landini
 */

public class MoviePreview extends JInternalFrame implements MouseListener {
	private static final long serialVersionUID = 1L;

	private static final String iconPath = "icons/player/";

	private IMoviePlayer player;

	private JButton fullRewButton;

	private JButton rewButton;

	private JButton playButton;

	private JButton ffButton;

	private JButton fullFFButton;

	private JToolBar toolBar;

	public MoviePreview(IMoviePlayer moviePlayer) {
		super();

		player = moviePlayer;
		player.setMouseControlEnabled(true);
		player.setKeyControlEnabled(true);

		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		int i = 0;

		gbc.weightx = 100;
		gbc.gridx = i++;
		toolBar.add(new JPanel());

		fullRewButton = createToolBarButton("rewind.png", "rewind_on.png");
		gbc.weightx = 0;
		gbc.gridx = i++;
		toolBar.add(fullRewButton);

		rewButton = createToolBarButton("indietro.png", "indietro_on.png");
		gbc.gridx = i++;
		toolBar.add(rewButton);

		playButton = createToolBarButton("play.png", "play_on.png");
		gbc.gridx = i++;
		toolBar.add(playButton);

		ffButton = createToolBarButton("avanti.png", "avanti_on.png");
		gbc.gridx = i++;
		toolBar.add(ffButton);

		fullFFButton = createToolBarButton("ff.png", "ff_on.png");
		gbc.gridx = i++;
		toolBar.add(fullFFButton);

		gbc.weightx = 100;
		gbc.gridx = i;
		toolBar.add(new JPanel());

		add(toolBar, java.awt.BorderLayout.SOUTH);
		add((Component) player, java.awt.BorderLayout.CENTER);

//		addComponentListener(new ComponentListener() {
//			@Override
//			public void componentHidden(ComponentEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void componentMoved(ComponentEvent e) {
//				Component preview = e.getComponent();
//				int x = preview.getX();
//				int y = preview.getY();
//				int width = preview.getWidth();
//				int height = preview.getHeight();
//				// Component desktopPane = preview.getParent();
//				// int desktopWidth = desktopPane.getWidth();
//				// int desktopHeight = desktopPane.getHeight();
//				if (x < 0) {
//					preview.setBounds(0, y, width, height);
//				} else if (y < 0) {
//					preview.setBounds(x, 0, width, height);
//					// } else if (x + width > desktopWidth) {
//					// preview.setBounds(desktopWidth - width, y, width,
//					// height);
//					// } else if (y + height > desktopHeight) {
//					// preview.setBounds(x, desktopHeight - height, width,
//					// height);
//				}
//			}
//
//			@Override
//			public void componentResized(ComponentEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void componentShown(ComponentEvent e) {
//				// TODO Auto-generated method stub
//
//			}
//		});

		setClosable(true);
//		setResizable(true);
		pack();
		setVisible(true);
	}

	private JButton createToolBarButton(String iconName, String rollOverIconName) {
		JButton button = new JButton();
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setIcon(new ImageIcon(iconPath + iconName));
		button.setRolloverEnabled(true);
		button.setRolloverIcon(new ImageIcon(iconPath + rollOverIconName));
		button.addMouseListener(this);

		return button;
	}

	public IMoviePlayer getMoviePlayer() {
		return player;
	}

	private void updatePlayButton() {
		int state = player.getCurrentState();
		if (state == IMoviePlayer.PLAY) {
			playButton.setIcon(new ImageIcon(iconPath + "pausa.png"));
			playButton.setRolloverIcon(new ImageIcon(iconPath + "pausa.png"));
		} else {
			playButton.setIcon(new ImageIcon(iconPath + "play.png"));
			playButton.setRolloverIcon(new ImageIcon(iconPath + "play_on.png"));
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("MoviePreview");

		int movieWidth = 640;
		int movieHeight = 480;
		int framerate = 25;
		float lon = 12.49f;
		float lat = 41.89f;
		// max = 16, min = 3
		int zStart = 16;
		int zEnd = 15;
		int length = 2000;

		int dim = zStart - zEnd + 1;
		MapLayer mapLayer = new MapLayer(movieWidth * 2, movieHeight * 2, dim);
		mapLayer.addEffect(new CyclicScaleEffect(mapLayer, zStart, zEnd));

		Layer pgLayer = null, telespazioLayer = null;
		try {
			pgLayer = new FixedLayer(0, 0);
			pgLayer.setImage(ImageIO.read(new File("icons/logo_visual.png")));
			pgLayer.setX(movieWidth - pgLayer.getImage().getWidth() - 10);
			pgLayer.setY(10);

			telespazioLayer = new FixedLayer(0, 0);
			telespazioLayer.setImage(ImageIO.read(new File(
					"icons/logo_telespazio_bianco.png")));
			telespazioLayer.setX(5);
			telespazioLayer.setY(10);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MoviePart moviePart = new MoviePart(length);
		moviePart.addLayer(mapLayer);

		Movie movie = new Movie(movieWidth * 2, movieHeight * 2, framerate);
		movie.addMoviePart(moviePart);

		movie.addLayer(pgLayer);
		movie.addLayer(telespazioLayer);

		AbstractImageLoader imageLoader = new SerialImageLoader(lon, lat,
				movieWidth * 2, movieHeight * 2, zStart, zEnd);
		Vector<BufferedImage> keyFrames = imageLoader.loadImages();
		for (int i = 0; i < keyFrames.size(); i++) {
			mapLayer.onImageLoaded(keyFrames.size() - i - 1, keyFrames.get(i));
		}

		IMoviePlayer player = new MoviePlayer(movie, movieWidth, movieHeight);
		MoviePreview preview = new MoviePreview(player);
		frame.add(preview);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				new Thread(new Runnable() {
					public void run() {
						System.exit(0);
					}
				}).start();
			}
		});
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JButton button = (JButton) e.getSource();
		if (button == fullRewButton) {
			player.fullRewind();
		} else if (button == playButton) {
			if (player.getCurrentState() == IMoviePlayer.PLAY) {
				player.pause();
			} else {
				player.play();
			}
		} else if (button == fullFFButton) {
			player.fullForward();
		}
		updatePlayButton();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		JButton button = (JButton) e.getSource();
		if (button == rewButton) {
			player.rewind(IMoviePlayer.SPEED_4X);
		} else if (button == ffButton) {
			player.fastForward(IMoviePlayer.SPEED_4X);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		JButton button = (JButton) e.getSource();
		if (button == rewButton || button == ffButton) {
			player.pause();
			updatePlayButton();
		}
	}

	public void destroy() {
		player.destroy();
		player = null;
	}
}
