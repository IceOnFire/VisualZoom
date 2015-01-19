package it.seat.visualzoom.player.layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import javax.media.opengl.GLAutoDrawable;

import com.sun.opengl.util.j2d.Overlay;

public class FixedLayer extends Layer {
	private Overlay overlay;
	private float alpha = 0.6f;
	
	public FixedLayer(int x, int y) {
		super(x, y);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		overlay = new Overlay(drawable);
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		Graphics2D g2d = overlay.createGraphics();

		// clear the overlay
		g2d.setComposite(AlphaComposite.Src);
		g2d.setColor(new Color(0, 0, 0, 0));
		g2d.fillRect(0, 0, width, height);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g2d.drawImage(image, x, y, width, height, null);
		
		/* scrive l'overlay */
		overlay.markDirty(0, 0, getWidth(), getHeight());
		overlay.drawAll();
		/* rilascia la risorsa grafica */
		g2d.dispose();
	}
}
