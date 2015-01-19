package it.seat.visualzoom.player.effects;

import it.seat.visualzoom.player.layers.Layer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

public class FlipEffect extends Effect {
	private float angle;
	private int direction;
	
	public FlipEffect(Layer layer, int direction) {
		super(layer);
		
		this.direction = direction;
	}

	@Override
	public void disable(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void enable(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		gl.glTranslatef(layer.getX(), layer.getY(), -layer.getZ());
		gl.glRotatef(angle*direction, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(-layer.getX(), -layer.getY(), layer.getZ());
	}

	@Override
	public void update(int time, int length) {
		angle = 90f * time / length;
	}
}
