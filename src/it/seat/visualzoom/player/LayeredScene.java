package it.seat.visualzoom.player;

import it.seat.visualzoom.player.layers.Layer;

import java.util.Vector;

import javax.media.opengl.GLAutoDrawable;

public abstract class LayeredScene {
	protected Vector<Layer> layers;

	public LayeredScene() {
		/* valori di default */
		layers = new Vector<Layer>();
	}

	public Vector<Layer> getLayers() {
		return layers;
	}

	public void addLayer(Layer layer) {
		layers.add(layer);
	}

	public void removeLayer(Layer layer) {
		layers.remove(layer);
	}

	public void init(GLAutoDrawable drawable) {
		for (Layer layer : layers) {
			layer.init(drawable);
		}
	}

	public void display(GLAutoDrawable drawable) {
		for (Layer layer : layers) {
			layer.display(drawable);
		}
	}
}
