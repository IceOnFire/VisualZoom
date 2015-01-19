package it.seat.visualzoom.player.effects;

import it.seat.visualzoom.player.layers.Layer;

import javax.media.opengl.GLAutoDrawable;

public class PositionScaleEffect extends ScaleEffect {
	private int x, y;
	
	public PositionScaleEffect(Layer layer, float scalingFactor) {
		this(layer, 0, 0);
		this.scalingFactor = scalingFactor;
	}
	
	public PositionScaleEffect(Layer layer, int zStart, int zEnd) {
		super(layer, zStart, zEnd);
		x = layer.getX();
		y = layer.getY();
	}
	
	@Override
	public void enable(GLAutoDrawable drawable) {
		layer.setX((int)(x*scalingFactor));
		layer.setY((int)(y*scalingFactor));
	}

	@Override
	public void update(int time, int length) {
		int direction = new Integer(zStart).compareTo(zEnd);
		int zoomLevels = Math.abs(zStart - zEnd);
		float zoomLength = 1f * length / zoomLevels;
		if (direction > 0) {
			scalingFactor = (float) Math.pow(2, time / zoomLength - zoomLevels)*2f;
		} else if (direction < 0) {
			scalingFactor = (float) Math.pow(2, -time / zoomLength)*2f;
		}
	}
}
