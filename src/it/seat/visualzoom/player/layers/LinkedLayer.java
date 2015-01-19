package it.seat.visualzoom.player.layers;

import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public abstract class LinkedLayer extends Layer {
	protected Texture texture;
	
	public LinkedLayer(int x, int y) {
		super(x, y);
	}

	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		setTexture(TextureIO.newTexture(image, false));
	}
}
