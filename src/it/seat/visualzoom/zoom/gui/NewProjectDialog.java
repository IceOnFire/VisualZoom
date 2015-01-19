/*
 * Creato il 24-mag-2007
 */
package it.seat.visualzoom.zoom.gui;

import it.seat.visual.frontend.SwingCloser;
import it.seat.visual.model.AbstractCloser;
import it.seat.visual.model.AbstractTile;
import it.seat.visual.model.CloserListener;
import it.seat.visual.model.coords.IJ;
import it.seat.visual.model.coords.LL;
import it.seat.visual.model.coords.XY;
import it.seat.visualzoom.logger.Log;
import it.seat.visualzoom.zoom.geocoding.GeocodingException;
import it.seat.visualzoom.zoom.geocoding.LBSClient;
import it.seat.visualzoom.zoom.geocoding.LBSResponse;
import it.seat.visualzoom.zoom.geocoding.Location;
import it.seat.visualzoom.zoom.gui.bean.VideoFormat;
import it.seat.visualzoom.zoom.gui.event.LocationSelectListener;
import it.seat.visualzoom.zoom.gui.event.NewProjectDialogListener;
import it.seat.visualzoom.zoom.gui.utils.FontManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * @author Leonardo Landini
 */
public class NewProjectDialog extends JDialog implements LocationSelectListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_DURATION = 15;
	// private static final float CLOSER_START_LAT = 45.4986f;// Palazzo SEAT
	// private static final float CLOSER_START_LON = 9.1223f;// Palazzo SEAT
	private static final float CLOSER_START_LAT = 41.95f;// Roma
	private static final float CLOSER_START_LON = 12.5f;// Roma
	private static final int DEFAULT_VIDEO_FORMAT_INDEX = 6;

	private javax.swing.JPanel jContentPane = null;

	private JPanel mainPanel = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JTabbedPane geoPanel = null;
	private JPanel optionPanel = null;
	private JPanel locationPanel = null;
	private JPanel startCloserPanel = null;
	private JPanel endCloserPanel = null;
	private SwingCloser startCloser;
	private SwingCloser endCloser;
	private JPanel coordPanel = null;
	private JLabel comLabel = null;
	private JLabel indLabel = null;
	private JLabel civLabel = null;
	private JLabel latLabel = null;
	private JLabel lonLabel = null;
	private JLabel durationLabel = null;
	private JLabel formatLabel = null;
	private JComboBox formatField = null;
	private JSpinner durationField = null;
	private JButton findButton = null;
	private JButton centerMapButton = null;
	private JTextField comField = null;
	private JTextField indField = null;
	private JTextField civField = null;
	private JTextField latField = null;
	private JTextField lonField = null;
	private JProgressBar waitBar = null;

	private DecimalFormat latLonFormat;
	private int lastLbsResponse;
	private List<NewProjectDialogListener> listeners;

	/**
	 * This is the default constructor
	 */
	public NewProjectDialog() {
		super();
		latLonFormat = new DecimalFormat("##.#####");
		listeners = new ArrayList<NewProjectDialogListener>();
		createVideoFormats();
		initialize();
	}

	private void createVideoFormats() {

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setTitle("Nuovo progetto...");
		this.setName("new ProjectDialog");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		// this.setSize(530, 420);
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		Rectangle screenRect = ge.getMaximumWindowBounds();
		setBounds(screenRect);

		startCloser = new SwingCloser(640, 480);
		startCloser.setViewMode(AbstractCloser.MODE_ORTO);
		// startCloser.setDrawGrid(true);
		startCloser.setDrawCenter(true);
		// startCloser.setMinZ(3);
		// startCloser.setMaxZ(16);
		startCloser.addMouseListener(this);
		startCloser.addCloserListener(new CloserListener() {
			@Override
			public void onMapCentered(XY center) {
				getLatField().setText(
						latLonFormat.format(startCloser.getCenter().getLat()));
				getLonField().setText(
						latLonFormat.format(startCloser.getCenter().getLon()));
			}

			@Override
			public void onMapDragged(XY center) {
				getGeoPanel().setSelectedIndex(1);
				endCloser.centerMap(new LL(startCloser.getCenter().getLon(),
						startCloser.getCenter().getLat()));
			}

			@Override
			public void onTileLoaded(AbstractTile tile, IJ coords) {
				// do nothing
			}

			@Override
			public void onViewModeChanged(int viewMode) {
				// do nothing
			}

			@Override
			public void onZoomChanged(int z) {
				// do nothing
			}
		});
		startCloser.centerMap(new LL(CLOSER_START_LON, CLOSER_START_LAT));

		endCloser = new SwingCloser(640, 480);
		endCloser.setViewMode(AbstractCloser.MODE_MAP);
		// endCloser.setDrawGrid(true);
		endCloser.setDrawCenter(true);
		// endCloser.setMinZ(3);
		// endCloser.setMaxZ(16);
		endCloser.addMouseListener(this);
		endCloser.addCloserListener(new CloserListener() {
			@Override
			public void onMapCentered(XY center) {
				getLatField().setText(
						latLonFormat.format(endCloser.getCenter().getLat()));
				getLonField().setText(
						latLonFormat.format(endCloser.getCenter().getLon()));
			}

			@Override
			public void onMapDragged(XY center) {
				getGeoPanel().setSelectedIndex(1);
				startCloser.centerMap(new LL(endCloser.getCenter().getLon(),
						endCloser.getCenter().getLat()));
			}

			@Override
			public void onTileLoaded(AbstractTile tile, IJ coords) {
				// do nothing
			}

			@Override
			public void onViewModeChanged(int viewMode) {
				// do nothing
			}

			@Override
			public void onZoomChanged(int z) {
				// do nothing
			}
		});
//		endCloser.centerMap(new LL(CLOSER_START_LON, CLOSER_START_LAT));

		setContentPane(getJContentPane());
		addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				fireCancelEvent();
			}
		});
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
			jContentPane.add(getButtonPanel(), java.awt.BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			GridLayout gridLayout1 = new GridLayout();
			mainPanel = new JPanel();
			mainPanel.setLayout(gridLayout1);
			gridLayout1.setRows(2);
			gridLayout1.setColumns(2);
			mainPanel.add(getGeoPanel(), null);
			mainPanel.add(getOptionPanel(), null);
			mainPanel.add(getStartCloserPanel(), null);
			mainPanel.add(getEndCloserPanel(), null);
		}
		return mainPanel;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButton(), null);
			buttonPanel.add(getCancelButton(), null);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton(new ConfirmNewProject());
			okButton.setText("OK");
		}
		return okButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(new CancelNewProject());
			cancelButton.setText("Annulla");
		}
		return cancelButton;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getGeoPanel() {
		if (geoPanel == null) {
			geoPanel = new JTabbedPane();
			geoPanel.addTab("Località", null, getLocationPanel(), null);
			geoPanel.addTab("Coordinate", null, getCoordPanel(), null);
		}
		return geoPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOptionPanel() {
		if (optionPanel == null) {
			optionPanel = new JPanel();
			optionPanel.add(getFormatLabel(), null);
			optionPanel.add(getFormatField(), null);
			optionPanel.add(getDurationLabel(), null);
			optionPanel.add(getDurationField(), null);
			// aggiusto il layout
			SpringLayout layout = new SpringLayout();
			layout.putConstraint(SpringLayout.WEST, getFormatLabel(), 20,
					SpringLayout.WEST, optionPanel);
			layout.putConstraint(SpringLayout.NORTH, getFormatLabel(), 20,
					SpringLayout.NORTH, optionPanel);

			layout.putConstraint(SpringLayout.WEST, getFormatField(), 0,
					SpringLayout.WEST, getFormatLabel());
			layout.putConstraint(SpringLayout.NORTH, getFormatField(), 20,
					SpringLayout.NORTH, getFormatLabel());

			layout.putConstraint(SpringLayout.WEST, getDurationLabel(), 0,
					SpringLayout.WEST, getFormatField());
			layout.putConstraint(SpringLayout.NORTH, getDurationLabel(), 20,
					SpringLayout.SOUTH, getFormatField());

			layout.putConstraint(SpringLayout.WEST, getDurationField(), 20,
					SpringLayout.EAST, getDurationLabel());
			layout.putConstraint(SpringLayout.NORTH, getDurationField(), 0,
					SpringLayout.NORTH, getDurationLabel());
			// aggiungo il layout al container
			optionPanel.setLayout(layout);
		}
		return optionPanel;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getLocationPanel() {
		if (locationPanel == null) {
			locationPanel = new JPanel();
			locationPanel.add(getComLabel(), null);
			locationPanel.add(getIndLabel(), null);
			locationPanel.add(getCivLabel(), null);
			locationPanel.add(getComField(), null);
			locationPanel.add(getIndField(), null);
			locationPanel.add(getCivField(), null);
			locationPanel.add(getFindButton(), null);
			locationPanel.add(getWaitBar(), null);
			// aggiusto il layout
			SpringLayout layout = new SpringLayout();
			layout.putConstraint(SpringLayout.WEST, getComLabel(), 5,
					SpringLayout.WEST, locationPanel);
			layout.putConstraint(SpringLayout.NORTH, getComLabel(), 10,
					SpringLayout.NORTH, locationPanel);

			layout.putConstraint(SpringLayout.WEST, getIndLabel(), 5,
					SpringLayout.WEST, locationPanel);
			layout.putConstraint(SpringLayout.NORTH, getIndLabel(), 15,
					SpringLayout.SOUTH, getComLabel());

			layout.putConstraint(SpringLayout.WEST, getCivLabel(), 5,
					SpringLayout.WEST, locationPanel);
			layout.putConstraint(SpringLayout.NORTH, getCivLabel(), 15,
					SpringLayout.SOUTH, getIndLabel());

			layout.putConstraint(SpringLayout.WEST, getComField(), 8,
					SpringLayout.EAST, getComLabel());
			layout.putConstraint(SpringLayout.NORTH, getComField(), 0,
					SpringLayout.NORTH, getComLabel());

			layout.putConstraint(SpringLayout.WEST, getIndField(), 7,
					SpringLayout.EAST, getIndLabel());
			layout.putConstraint(SpringLayout.NORTH, getIndField(), 0,
					SpringLayout.NORTH, getIndLabel());

			layout.putConstraint(SpringLayout.WEST, getCivField(), 8,
					SpringLayout.EAST, getCivLabel());
			layout.putConstraint(SpringLayout.NORTH, getCivField(), 0,
					SpringLayout.NORTH, getCivLabel());

			layout.putConstraint(SpringLayout.EAST, getFindButton(), 0,
					SpringLayout.EAST, getCivField());
			layout.putConstraint(SpringLayout.NORTH, getFindButton(), 15,
					SpringLayout.SOUTH, getCivField());

			layout.putConstraint(SpringLayout.EAST, getWaitBar(), 0,
					SpringLayout.EAST, getFindButton());
			layout.putConstraint(SpringLayout.NORTH, getWaitBar(), 0,
					SpringLayout.NORTH, getFindButton());
			// aggiungo il layout al container
			locationPanel.setLayout(layout);
		}
		return locationPanel;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getCoordPanel() {
		if (coordPanel == null) {
			coordPanel = new JPanel();
			coordPanel.add(getLatLabel(), null);
			coordPanel.add(getLonLabel(), null);
			coordPanel.add(getLatField(), null);
			coordPanel.add(getLonField(), null);
			coordPanel.add(getCenterMapButton(), null);
			// //aggiusto il layout
			SpringLayout layout = new SpringLayout();
			layout.putConstraint(SpringLayout.WEST, getLatLabel(), 5,
					SpringLayout.WEST, coordPanel);
			layout.putConstraint(SpringLayout.NORTH, getLatLabel(), 10,
					SpringLayout.NORTH, coordPanel);

			layout.putConstraint(SpringLayout.WEST, getLonLabel(), 5,
					SpringLayout.WEST, coordPanel);
			layout.putConstraint(SpringLayout.NORTH, getLonLabel(), 15,
					SpringLayout.SOUTH, getLatLabel());

			layout.putConstraint(SpringLayout.WEST, getLatField(), 8,
					SpringLayout.EAST, getLonLabel());
			layout.putConstraint(SpringLayout.NORTH, getLatField(), 0,
					SpringLayout.NORTH, getLatLabel());

			layout.putConstraint(SpringLayout.WEST, getLonField(), 8,
					SpringLayout.EAST, getLonLabel());
			layout.putConstraint(SpringLayout.NORTH, getLonField(), 0,
					SpringLayout.NORTH, getLonLabel());

			layout.putConstraint(SpringLayout.WEST, getCenterMapButton(), 8,
					SpringLayout.EAST, getLonLabel());
			layout.putConstraint(SpringLayout.NORTH, getCenterMapButton(), 15,
					SpringLayout.SOUTH, getLonLabel());
			// //aggiungo il layout al container
			coordPanel.setLayout(layout);
		}
		return coordPanel;
	}

	// private JCloser getEndCloser() {
	// if (endCloser == null) {
	// endCloser = new JCloser(JCloser.MODE_MAP);
	// endCloser.setSize(250, 150);
	// endCloser.setMinZ(3);
	// endCloser.setMaxZ(16);
	// endCloser.centerMap(CLOSER_START_LAT, CLOSER_START_LON);
	// endCloser.setDrawingCenter(true);
	// getEndCloser().addListener(new JCloserListener() {
	// public void onMapCentered(int center_x, int center_y) {
	// getLatField().setText(
	// latLonFormat.format(getEndCloser()
	// .getCurrentCenterLat()));
	// getLonField().setText(
	// latLonFormat.format(getEndCloser()
	// .getCurrentCenterLon()));
	// }
	//
	// public void onMapDragged(int center_x, int center_y) {
	// getGeoPanel().setSelectedIndex(1);
	// startCloser.centerMap(new LL((float) getEndCloser()
	// .getCurrentCenterLon(), (float) getEndCloser()
	// .getCurrentCenterLat()));
	// getLatField().setText(
	// latLonFormat.format(getEndCloser()
	// .getCurrentCenterLat()));
	// getLonField().setText(
	// latLonFormat.format(getEndCloser()
	// .getCurrentCenterLon()));
	// }
	//
	// public void onZoomChanged(int z) {
	// // nothing to do
	// }
	// });
	// }
	// return endCloser;
	// }

	private JPanel getStartCloserPanel() {
		if (startCloserPanel == null) {
			startCloserPanel = new JPanel();
			JRadioButton mapButton = new JRadioButton();
			mapButton.setAction(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					startCloser.setViewMode(AbstractCloser.MODE_MAP);
				}
			});
			mapButton.setText("Mappa");
			JRadioButton ortoButton = new JRadioButton();
			ortoButton.setAction(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					startCloser.setViewMode(AbstractCloser.MODE_ORTO);
				}
			});
			ortoButton.setText("Foto");
			// JRadioButton mixedButton = new JRadioButton();
			// mixedButton.setAction(new AbstractAction() {
			// private static final long serialVersionUID = 1L;
			//
			// public void actionPerformed(ActionEvent e) {
			// startCloser.setViewMode(JCloser.MODE_MIXED);
			// }
			// });
			// mixedButton.setText("Mista");
			ButtonGroup bgroup = new ButtonGroup();
			JPanel radioPanel = new JPanel(new FlowLayout());
			radioPanel.add(mapButton);
			radioPanel.add(ortoButton);
			// radioPanel.add(mixedButton);
			bgroup.add(mapButton);
			bgroup.add(ortoButton);
			// bgroup.add(mixedButton);
			ortoButton.setSelected(true);
			JLabel l = new JLabel("Livello iniziale");
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setPreferredSize(new Dimension(20, 20));
			JLabel spacer = new JLabel();
			spacer.setPreferredSize(new Dimension(2, 5));
			startCloserPanel.setLayout(new BorderLayout());
			startCloserPanel.add(l, BorderLayout.NORTH);
			startCloserPanel.add(startCloser, BorderLayout.CENTER);
			startCloserPanel.add(radioPanel, BorderLayout.SOUTH);
			startCloserPanel.add(spacer, BorderLayout.EAST);
			// startCloserPanel.add(spacer,BorderLayout.WEST);
		}
		return startCloserPanel;
	}

	private JPanel getEndCloserPanel() {
		if (endCloserPanel == null) {
			endCloserPanel = new JPanel();
			JRadioButton mapButton = new JRadioButton();
			mapButton.setAction(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					endCloser.setViewMode(AbstractCloser.MODE_MAP);
				}
			});
			mapButton.setText("Mappa");
			JRadioButton ortoButton = new JRadioButton();
			ortoButton.setAction(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					endCloser.setViewMode(AbstractCloser.MODE_ORTO);
				}
			});
			ortoButton.setText("Foto");
			// JRadioButton mixedButton = new JRadioButton();
			// mixedButton.setAction(new AbstractAction() {
			// private static final long serialVersionUID = 1L;
			//
			// public void actionPerformed(ActionEvent e) {
			// getEndCloser().setViewMode(JCloser.MODE_MIXED);
			// }
			// });
			// mixedButton.setText("Mista");
			ButtonGroup bgroup = new ButtonGroup();
			JPanel radioPanel = new JPanel(new FlowLayout());
			radioPanel.add(mapButton);
			radioPanel.add(ortoButton);
			// radioPanel.add(mixedButton);
			bgroup.add(mapButton);
			bgroup.add(ortoButton);
			// bgroup.add(mixedButton);
			mapButton.setSelected(true);
			JLabel l = new JLabel("Livello finale");
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setPreferredSize(new Dimension(20, 20));
			JLabel spacer = new JLabel();
			spacer.setPreferredSize(new Dimension(2, 5));
			endCloserPanel.setLayout(new BorderLayout());
			endCloserPanel.add(l, BorderLayout.NORTH);
			endCloserPanel.add(endCloser, BorderLayout.CENTER);
			endCloserPanel.add(radioPanel, BorderLayout.SOUTH);
			endCloserPanel.add(spacer, BorderLayout.WEST);
			// endCloserPanel.add(spacer,BorderLayout.EAST);
		}
		return endCloserPanel;
	}

	private JComboBox getFormatField() {
		if (formatField == null) {
			formatField = new JComboBox();
			formatField.addItem(new VideoFormat("PC1", "320 x 240", 320, 240,
					30));
			formatField.addItem(new VideoFormat("PC2", "512 x 384", 512, 384,
					30));
			formatField.addItem(new VideoFormat("PC3", "640 x 480", 640, 480,
					30));
			// formatField.addItem(new VideoFormat("PC4","800 x
			// 600",800,600,30));
			// formatField.addItem(new VideoFormat("PC5","1024 x
			// 768",1024,768,30));

			formatField.addItem(new VideoFormat("NTSCDV", "NTSC DV, 720 x 480",
					720, 480, 30));
			// formatField.addItem(new VideoFormat("NTSCDVWS","NTSC DV
			// Widescreen, 720 x 480",720,480,30));
			formatField.addItem(new VideoFormat("NTSCD1", "NTSC D1, 720 x 486",
					720, 486, 30));
			formatField.addItem(new VideoFormat("NTSCD1SP",
					"NTSC D1 Square Pix, 720 x 540", 720, 540, 30));

			formatField.addItem(new VideoFormat("PALD1DV",
					"PAL D1/DV, 720 x 576", 720, 576, 25));
			formatField.addItem(new VideoFormat("PALD1DVINT",
					"PAL D1/DV Interlaced, 720 x 576", 720, 576, 50));
			// formatField.addItem(new VideoFormat("PALD1DVWS","PAL D1/DV
			// Widescreen, 720 x 576",720,576,25));
			// formatField.addItem(new VideoFormat("PALD1DVSP","PAL D1/DV Square
			// Pix, 768 x 576",768,576,25));

			// formatField.addItem(new VideoFormat("HDV1","HDV, 1280 x
			// 720",1280,720,25));
			// formatField.addItem(new VideoFormat("HDV1","HDV, 1440 x
			// 1080",1440,1080,25));
			// formatField.addItem(new VideoFormat("HDV1","HDV, 1920 x
			// 1080",1920,1080,25));

			formatField.setSelectedIndex(DEFAULT_VIDEO_FORMAT_INDEX);
			formatField.setFont(FontManager.labelNormal());
		}
		return formatField;
	}

	private JSpinner getDurationField() {
		if (durationField == null) {
			durationField = new JSpinner();
			durationField.setModel(new SpinnerNumberModel(DEFAULT_DURATION, 1,
					60, 1));
			durationField.setPreferredSize(new Dimension(50, 20));
			durationField.setFont(FontManager.labelNormal());
		}
		return durationField;
	}

	private JButton getFindButton() {
		if (findButton == null) {
			findButton = new JButton(new FindLocation());
			findButton.setText("Cerca");
		}
		return findButton;
	}

	private JButton getCenterMapButton() {
		if (centerMapButton == null) {
			centerMapButton = new JButton(new CenterMapAction());
			centerMapButton.setText("Cerca");
		}
		return centerMapButton;
	}

	private JTextField getComField() {
		if (comField == null) {
			comField = new JTextField();
			comField.setFont(FontManager.labelNormal());
			comField.setColumns(16);
		}
		return comField;
	}

	private JTextField getIndField() {
		if (indField == null) {
			indField = new JTextField();
			indField.setFont(FontManager.labelNormal());
			indField.setColumns(16);
		}
		return indField;
	}

	private JTextField getCivField() {
		if (civField == null) {
			civField = new JTextField();
			civField.setFont(FontManager.labelNormal());
			civField.setColumns(16);
		}
		return civField;
	}

	private JTextField getLatField() {
		if (latField == null) {
			latField = new JTextField();
			latField.setFont(FontManager.labelNormal());
			latField.setColumns(10);
		}
		return latField;
	}

	private JTextField getLonField() {
		if (lonField == null) {
			lonField = new JTextField();
			lonField.setFont(FontManager.labelNormal());
			lonField.setColumns(10);
		}
		return lonField;
	}

	// labels
	private JLabel getFormatLabel() {
		if (formatLabel == null) {
			formatLabel = new JLabel();
			formatLabel.setText("Formato video");
			formatLabel.setFont(FontManager.labelNormal());
		}
		return formatLabel;
	}

	private JLabel getComLabel() {
		if (comLabel == null) {
			comLabel = new JLabel();
			comLabel.setText("Comune");
			comLabel.setFont(FontManager.labelNormal());
		}
		return comLabel;
	}

	private JLabel getIndLabel() {
		if (indLabel == null) {
			indLabel = new JLabel();
			indLabel.setText("Indirizzo");
			indLabel.setFont(FontManager.labelNormal());
		}
		return indLabel;
	}

	private JLabel getCivLabel() {
		if (civLabel == null) {
			civLabel = new JLabel();
			civLabel.setText("N. Civico");
			civLabel.setFont(FontManager.labelNormal());
		}
		return civLabel;
	}

	private JLabel getLatLabel() {
		if (latLabel == null) {
			latLabel = new JLabel();
			latLabel.setText("Latitudine");
			latLabel.setFont(FontManager.labelNormal());
		}
		return latLabel;
	}

	private JLabel getLonLabel() {
		if (lonLabel == null) {
			lonLabel = new JLabel();
			lonLabel.setText("Longitudine");
			lonLabel.setFont(FontManager.labelNormal());
		}
		return lonLabel;
	}

	private JLabel getDurationLabel() {
		if (durationLabel == null) {
			durationLabel = new JLabel();
			durationLabel.setText("Durata [s]");
			durationLabel.setFont(FontManager.labelNormal());
		}
		return durationLabel;
	}

	private JProgressBar getWaitBar() {
		if (waitBar == null) {
			waitBar = new JProgressBar();
			waitBar.setIndeterminate(true);
			waitBar.setVisible(false);
		}
		return waitBar;
	}

	// LocationSelectListener
	public void onLocationSelected(Location l) {
		getComField().setText(l.getCom());
		getIndField().setText(l.getAddress());
		getCivField().setText(l.getCiv());
		if (lastLbsResponse == LBSResponse.TYPE_OK) {
			startCloser.reset();
			startCloser.centerMap(new LL(l.getLon(), l.getLat()));
			endCloser.reset();
			endCloser.centerMap(new LL(l.getLon(), l.getLat()));
		} else {
			getFindButton().doClick();
		}
		getFindButton().setVisible(true);
		getWaitBar().setVisible(false);
	}

	public void onLocationSelectCancelled() {
		getFindButton().setVisible(true);
		getWaitBar().setVisible(false);
	}

	// events
	public void addListener(NewProjectDialogListener l) {
		listeners.add(l);
	}

	private void fireConfirmEvent() {
		int w = ((VideoFormat) getFormatField().getSelectedItem()).getWidth();
		int h = ((VideoFormat) getFormatField().getSelectedItem()).getHeight();
		int z1 = startCloser.getZoomLevel();
		int z2 = endCloser.getZoomLevel() + 1;
		System.out.println(z1 + ", " + z2);
		double lat = endCloser.getCenter().getLat();
		double lon = endCloser.getCenter().getLon();
		Location l = new Location((float) lat, (float) lon);
		int len = ((Integer) getDurationField().getValue()).intValue();
		for (NewProjectDialogListener listener : listeners) {
			listener.onNewProjectConfirm(l, w, h, z1, z2, len);
		}
	}

	private void fireCancelEvent() {
		for (NewProjectDialogListener listener : listeners) {
			listener.onNewProjectCancel();
		}
	}

	// Editors
	class IntegerComboEditor extends BasicComboBoxEditor {
		Integer lastValidValue = null;

		public void setItem(Object anItem) {
			try {
				Log.getLogger().finer("Trying to decode " + anItem);
				lastValidValue = Integer.decode(String.valueOf(anItem));
				Log.getLogger().finer(
						"Succesful. Last valid value: " + lastValidValue);
			} catch (Exception e) {
				Log.getLogger().finer(
						"Error. Last valid value: " + lastValidValue);
			} finally {
				if (lastValidValue != null) {
					super.setItem(lastValidValue.toString());
				}
			}
		}

		// public Object getItem(){
		// return lastValidValue.toString();
		// }
	}

	// Actions
	class FindLocation extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			Thread t = new Thread() {
				public void run() {
					try {
						LBSClient lbs = new LBSClient(
								new URL(
										"http://lbs.tuttocitta.it/WS_TCOL.asmx/Addr_Map_U"));
						LBSResponse resp = lbs.getLocations(getComField()
								.getText(), getIndField().getText(),
								getCivField().getText());
						lastLbsResponse = resp.getType();
						if (lastLbsResponse == LBSResponse.TYPE_COMUNE_NON_UNIVOCO
								|| lastLbsResponse == LBSResponse.TYPE_INDIRIZZO_NON_UNIVOCO) {
							LocationSelectDialog lsd = new LocationSelectDialog(
									resp.getLocations());
							// calcola la posizione della finestra
							int x = NewProjectDialog.this.getLocation().x;
							int y = NewProjectDialog.this.getLocation().y;
							int w = lsd.getWidth();
							int h = lsd.getHeight();
							lsd.setLocation(x + w / 3, y + h / 3);
							lsd.setModal(true);
							lsd.addListener(NewProjectDialog.this);
							lsd.setVisible(true);
						} else if (lastLbsResponse == LBSResponse.TYPE_OK) {
							onLocationSelected(resp.getLocations()[0]);
						} else {
							// errore
							onLocationSelectCancelled();
							String title = getComField().getText();
							if (getIndField().getText() != null
									&& getIndField().getText().length() > 0)
								title += " - " + getIndField().getText();
							if (getCivField().getText() != null
									&& getCivField().getText().length() > 0)
								title += ", " + getCivField().getText();
							JOptionPane.showMessageDialog(
									NewProjectDialog.this,
									"Indirizzo non trovato.", title,
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (MalformedURLException exc) {
						getFindButton().setVisible(true);
						getWaitBar().setVisible(false);
						JOptionPane.showMessageDialog(NewProjectDialog.this,
								exc.toString(), "Errore di connessione",
								JOptionPane.ERROR_MESSAGE);
					} catch (GeocodingException exc) {
						getFindButton().setVisible(true);
						getWaitBar().setVisible(false);
						JOptionPane.showMessageDialog(NewProjectDialog.this,
								exc.toString(), "Errore di geocodifica",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			};
			t.start();
			getFindButton().setVisible(false);
			getWaitBar().setVisible(true);
		}
	}

	class CenterMapAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			boolean latOk = false;
			boolean lonOk = false;
			try {
				float lat = latLonFormat.parse(getLatField().getText())
						.floatValue();
				latOk = true;
				float lon = latLonFormat.parse(getLonField().getText())
						.floatValue();
				lonOk = true;
				// controlla che il posto sia in europa
				if (lat > 30 && lat < 60 && lon > -9 && lon < 30) {
					startCloser.centerMap(new LL(lon, lat));
					endCloser.centerMap(new LL(lon, lat));
				} else {
					JOptionPane.showMessageDialog(NewProjectDialog.this,
							"Il punto selezionato non si trova in Europa",
							"Impossibile centrare la mappa",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (ParseException exc) {
				if (!latOk) {
					JOptionPane
							.showMessageDialog(
									NewProjectDialog.this,
									"Inserire un valore corretto per il campo 'latitudine'",
									"Impossibile centrare la mappa",
									JOptionPane.ERROR_MESSAGE);
				} else if (!lonOk) {
					JOptionPane
							.showMessageDialog(
									NewProjectDialog.this,
									"Inserire un valore corretto per il campo 'longitudine'",
									"Impossibile centrare la mappa",
									JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	class ConfirmNewProject extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			getFindButton().setVisible(true);
			getWaitBar().setVisible(false);
			int z1 = startCloser.getZoomLevel();
			int z2 = endCloser.getZoomLevel();
			if (z1 > z2) {
				NewProjectDialog.this.dispose();
				fireConfirmEvent();
			} else {
				JOptionPane
						.showMessageDialog(
								NewProjectDialog.this,
								"Il livello di zoom finale deve essere superiore a quello iniziale.",
								"Impossibile generare il filmato",
								JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	class CancelNewProject extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			getFindButton().setVisible(true);
			getWaitBar().setVisible(false);
			NewProjectDialog.this.dispose();
			fireCancelEvent();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getComponent() == startCloser) {
			endCloser.update();
		} else if (e.getComponent() == endCloser) {
			startCloser.update();
		}
	}
}
