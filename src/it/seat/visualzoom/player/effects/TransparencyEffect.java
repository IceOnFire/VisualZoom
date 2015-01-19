package it.seat.visualzoom.player.effects;

import it.seat.visualzoom.player.layers.Layer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

public class TransparencyEffect extends Effect {
	private int direction;
	private float alpha;
	
	public TransparencyEffect(Layer layer, int direction) {
		super(layer);
		
		this.direction = direction;
	}

	@Override
	public void enable(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, alpha);
	}
	
	@Override
	public void disable(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	@Override
	public void update(int time, int length) {
		if (direction > 0) {
			alpha = (float) Math.pow(2, 1f * time / length) - 1;
		} else if (direction < 0) {
			alpha = (float) Math.pow(2, 1 - 1f * time / length) - 1;
		}
//		if (direction > 0) {
//			alpha = 1f * time / length;
//		} else if (direction < 0) {
//			alpha = 1 - 1f * time / length;
//		}
	}
}
