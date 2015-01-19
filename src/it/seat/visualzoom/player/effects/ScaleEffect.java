package it.seat.visualzoom.player.effects;

import it.seat.visualzoom.player.layers.Layer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

public class ScaleEffect extends Effect {
	protected int zStart, zEnd;
	protected float scalingFactor;
	
	public ScaleEffect(Layer layer, float scalingFactor) {
		this(layer, 0, 0);
		this.scalingFactor = scalingFactor;
	}
	
	public ScaleEffect(Layer layer, int zStart, int zEnd) {
		super(layer);
		
		this.zStart = zStart;
		this.zEnd = zEnd;
	}

	@Override
	public void enable(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		gl.glScalef(scalingFactor, scalingFactor, 1);
	}
	
	@Override
	public void disable(GLAutoDrawable drawable) {
		
	}

	@Override
	public void update(int time, int length) {
		int direction = new Integer(zStart).compareTo(zEnd);
		/* lo scalingFactor è funzione esponenziale del tempo */
		if (direction > 0) {
			scalingFactor = (float) Math.pow(2, 1f*time / length);
		} else if (direction < 0) {
			scalingFactor = (float) Math.pow(2, 1 - 1f*time / length);
		}
	}
}
