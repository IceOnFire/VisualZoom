package it.seat.visualzoom.player.layers;

import it.seat.visualzoom.player.effects.Effect;

import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

public class DoubleFacedSquareLayer extends SquareLayer {
	private BufferedImage backImage;
	private Texture backTexture;
	
	public DoubleFacedSquareLayer(int x, int y) {
		super(x, y);
	}
	
	public BufferedImage getBackImage() {
		return backImage;
	}

	public void setBackImage(BufferedImage backImage) {
		this.backImage = backImage;
	}

	public Texture getBackTexture() {
		return backTexture;
	}

	public void setBackTexture(Texture backTexture) {
		this.backTexture = backTexture;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		setBackTexture(TextureIO.newTexture(backImage, false));
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
		
		/* front texture */
		texture.enable();
		texture.bind();
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		TextureCoords coords = texture.getImageTexCoords();

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(coords.left(), coords.bottom());
		gl.glVertex3f(left, bottom, -z-1);
		gl.glTexCoord2f(coords.left(), coords.top());
		gl.glVertex3f(left, top, -z-1);
		gl.glTexCoord2f(coords.right(), coords.top());
		gl.glVertex3f(right, top, -z-1);
		gl.glTexCoord2f(coords.right(), coords.bottom());
		gl.glVertex3f(right, bottom, -z-1);
		gl.glEnd();
		texture.disable();
		
		/* back texture */
		backTexture.enable();
		backTexture.bind();
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		TextureCoords backCoords = backTexture.getImageTexCoords();
		
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(backCoords.left(), backCoords.bottom());
		gl.glVertex3f(right, bottom, -z+1);
		gl.glTexCoord2f(backCoords.left(), backCoords.top());
		gl.glVertex3f(right, top, -z+1);
		gl.glTexCoord2f(backCoords.right(), backCoords.top());
		gl.glVertex3f(left, top, -z+1);
		gl.glTexCoord2f(backCoords.right(), backCoords.bottom());
		gl.glVertex3f(left, bottom, -z+1);
		gl.glEnd();
		backTexture.disable();
		
		for (Effect effect : effects) {
			effect.disable(drawable);
		}
		
		gl.glPopMatrix();
	}
}
