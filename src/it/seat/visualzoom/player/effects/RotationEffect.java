package it.seat.visualzoom.player.effects;

import it.seat.visualzoom.player.layers.Layer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

public class RotationEffect extends Effect {
	public final static int X_AXIS = 0;
	public final static int Y_AXIS = 1;
	public final static int Z_AXIS = 2;

	private int axis;
	private float angle;

	public RotationEffect(Layer layer, int axis, float angle) {
		super(layer);

		this.axis = axis;
		this.angle = angle;
	}

	@Override
	public void disable(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void enable(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		float[] axes = new float[3];
		axes[axis] = 1.0f;
		gl.glTranslatef(layer.getX(), layer.getY(), -layer.getZ());
		gl.glRotatef(angle, axes[0], axes[1], axes[2]);
		gl.glTranslatef(-layer.getX(), -layer.getY(), layer.getZ());
	}

	@Override
	public void update(int time, int length) {
		// TODO Auto-generated method stub

	}
}
