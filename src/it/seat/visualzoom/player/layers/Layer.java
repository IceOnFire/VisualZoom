package it.seat.visualzoom.player.layers;

import it.seat.visualzoom.player.effects.Effect;

import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.media.opengl.GLAutoDrawable;

public abstract class Layer {
	protected int x;
	protected int y;
	protected float z;
	/** Dimensioni in pixel. */
	protected int width, height;
	protected BufferedImage image;
	protected Vector<Effect> effects;
	
	public Layer(int x, int y) {
		this.x = x;
		this.y = y;
		
		/* inizializzazione */
		effects = new Vector<Effect>();
		
		/* impostazioni di default */
		z = 0;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
		width = image.getWidth();
		height = image.getHeight();
	}
	
	public void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	public Vector<Effect> getEffects() {
		return effects;
	}
	
	public void update(int time, int length) {
		for (Effect effect : effects) {
			effect.update(time, length);
		}
	}
	
	public abstract void init(GLAutoDrawable drawable);
	
	public abstract void display(GLAutoDrawable drawable);
	
	public String toString() {
		return "[x: " + x + ", y: " + y + "]";
	}

	public void destroy() {
		for (Effect effect : effects) {
			effect.destroy();
			effect = null;
		}
	}
}
