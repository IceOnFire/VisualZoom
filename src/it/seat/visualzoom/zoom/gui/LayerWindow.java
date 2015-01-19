package it.seat.visualzoom.zoom.gui;

import it.seat.visualzoom.player.layers.Layer;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class LayerWindow extends JInternalFrame {
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JList layerList = null;
	private Vector<Layer> layers;

	public LayerWindow(Vector<Layer> layers) {
		super();
		this.layers = layers;
		initialize();
	}

	private void initialize() {
		this.setTitle("Titoli");
		this.setName("TitleWindow");
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setSize(200, 320);
		this.setContentPane(getJContentPane());
	}

	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new java.awt.BorderLayout());
			jContentPane.add(getList(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	private JList getList() {
		if (layerList == null) {
			layerList = new JList(layers);
		}
		return layerList;
	}

	public void addLayer(Layer layer) {
		((DefaultListModel) getList().getModel()).addElement(layer);
	}
}
