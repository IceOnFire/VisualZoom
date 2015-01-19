package it.seat.visualzoom.player.layers;


import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

public class CylinderLayer extends LinkedLayer {
	private GLU glu;
	private GLUquadric quadric;
	private int base;
	private int height;
	
	public CylinderLayer(int x, int y, int base, int height) {
		super(x, y);
		this.base = base;
		this.height = height;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		glu = new GLU();
		quadric = glu.gluNewQuadric();
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		glu.gluQuadricTexture(quadric, true);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		
		gl.glTranslatef(0.0f, 0.0f, -0.5f);
		
		texture.enable();
		texture.bind();
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        
		glu.gluCylinder(quadric, base, base, height, 32, 32);
        texture.disable();
	}
}
