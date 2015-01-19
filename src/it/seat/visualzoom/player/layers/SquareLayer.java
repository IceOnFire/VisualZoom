package it.seat.visualzoom.player.layers;

import it.seat.visualzoom.player.effects.Effect;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.texture.TextureCoords;

public class SquareLayer extends LinkedLayer {
	public SquareLayer(int x, int y) {
		super(x, y);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		gl.glPushMatrix();
		
		for (Effect effect : effects) {
			effect.enable(drawable);
		}
		
		float left = x - width / 2f;
		float bottom = y - height / 2f;
		float right = x + width / 2f;
		float top = y + height / 2f;

		texture.enable();
		texture.bind();
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		TextureCoords coords = texture.getImageTexCoords();

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(coords.left(), coords.bottom());
		gl.glVertex3f(left, bottom, -z);
		gl.glTexCoord2f(coords.left(), coords.top());
		gl.glVertex3f(left, top, -z);
		gl.glTexCoord2f(coords.right(), coords.top());
		gl.glVertex3f(right, top, -z);
		gl.glTexCoord2f(coords.right(), coords.bottom());
		gl.glVertex3f(right, bottom, -z);
		gl.glEnd();
		texture.disable();
		
		for (Effect effect : effects) {
			effect.disable(drawable);
		}
		
		gl.glPopMatrix();
	}
}
