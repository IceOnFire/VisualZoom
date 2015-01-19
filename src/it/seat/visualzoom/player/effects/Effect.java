package it.seat.visualzoom.player.effects;

import it.seat.visualzoom.player.layers.Layer;

import javax.media.opengl.GLAutoDrawable;

public abstract class Effect {
	protected Layer layer;
	
	protected Effect(Layer layer) {
		this.layer = layer;
	}
	
	public abstract void update(int time, int length);
	
	public abstract void enable(GLAutoDrawable drawable);
	
	public abstract void disable(GLAutoDrawable drawable);

	public void destroy() {
		layer = null;
	}
}
