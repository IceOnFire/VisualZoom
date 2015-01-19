package it.seat.visualzoom.zoom.video;

import it.seat.visualzoom.logger.Log;
import it.seat.visualzoom.zoom.gui.bean.Title;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

//import com.jhlabs.image.MotionBlurOp;

public class FrameGenerator {

	BufferedImage[] keyFrames; // mappe a diversa risoluzione
	int width; // larghezza dei frame generati
	int height; // altezza dei frame generati
	float fps; // frame al secondo: 25 PAL, 29.9 NTSC
	float length; // lunghezza totale filmato in secondi
	// valori calcolati
	float ratio; // rapporto di dimensioni dell'immagine
	int fpk; // numero frame generati da ogni fotogramma chiave
	// dati
	Frame[] frames;
	ArrayList<Title> titles;
	BufferedImage logo;
	BufferedImage frameImage;
	boolean realized;

	// MotionBlurOp blur;

	/**
	 * @param keyFrames
	 * @param width
	 * @param heigth
	 * @param fps
	 * @param length
	 */
	public FrameGenerator(BufferedImage[] keyFrames, BufferedImage logo,
			int width, int height, float fps, float length,
			ArrayList<Title> titles) {
		super();
		this.realized = false;
		this.keyFrames = keyFrames;
		this.width = width;
		this.height = height;
		this.fps = fps;
		this.length = length;
		// calcolo valori
		this.ratio = (float) width / height;
		this.fpk = (int) (length * fps / keyFrames.length);
		// frames = new Frame[(int)(this.fps * this.length)];
		frames = new Frame[fpk * keyFrames.length];
		this.logo = logo;
		this.frameImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		this.frameImage.setAccelerationPriority(1);
		Log.getLogger().finer(
				"Movie Info [Width = " + width + ", Heigth = " + height
						+ ", N.KeyFrames = " + keyFrames.length + ", fps = "
						+ fps + ", fpk = " + fpk + ", length = " + length
						+ ", N.Frames = " + frames.length + "]");
		// blur = new MotionBlurOp();
		// blur.setZoom(0.0367f);
		// for (int i = 0; i < this.keyFrames.length; i++) {
		// blur.filter(this.keyFrames[i],this.keyFrames[i]);
		// }
		this.titles = titles;
	}

	/*
	 * public void realize(){ double k = computeK(width,height,fpk); for(int
	 * i=0; i<keyFrames.length; i++){ float dx = 0; float w = (float)width;
	 * float sf = 0.5f; for(int j=0; j<fpk; j++){ frames[i*fpk+j] = new
	 * Frame(keyFrames[i],dx); dx += k/sf; sf = (w+2*dx)/(2*w); } }
	 * this.realized = true; }
	 * 
	 * public Frame getFrame(int i){ return frames[i]; }
	 */
	public void realize() {
		double k = computeK(width, height, fpk);
		double ratio = (double) width / height;
		double pow_ratio = Math.pow(ratio, 2);
		for (int i = 0; i < keyFrames.length; i++) {
			double ds = 0;
			float dx = 0;
			float w = width;
			float sf = 0.5f;
			for (int j = 0; j < fpk; j++) {
				frames[i * fpk + j] = new Frame(keyFrames[i], dx);
				ds += k / sf;
				dx = (float) Math.sqrt(pow_ratio * Math.pow(ds, 2)
						/ (1 + pow_ratio));
				sf = (w + 2 * dx) / (2 * w);
			}
		}
		this.realized = true;
	}

	/*
	 * private static float computeK(int width, int height, int fpk){ //calcola
	 * di quanti pixel devo spostare ogni frame per avere velocit� costante
	 * float error = Float.MAX_VALUE; float up_k = (int)(width/2)+1; float
	 * down_k = 0; float k = 1;//guess //int iterations = 0; while(error>0.01){
	 * //iterations++; float dx = 0; float w = (float)width; float sf = 0.5f;
	 * for(int i = 0; i<fpk; i++){ dx += k/sf; sf = (w+2*dx)/(2*w); if(dx>w/2)
	 * break; } error = Math.abs(dx-w/2); if(dx>w/2) { up_k = k; k =
	 * (k+down_k)/2; } else { down_k = k; k=(k+up_k)/2; } }
	 * //Log.getLogger().finer("Iterazioni: "+iterations); return k; }
	 */
	private static double computeK(int width, int height, int fpk) {
		// calcola di quanti pixel devo spostare ogni frame per avere velocit�
		// costante
		double error = Double.MAX_VALUE;
		double up_k = width / 2 + 1;
		double down_k = 0;
		double k = 1;// guess
		double w = width;
		double h = height;
		double R2 = Math.pow(w / h, 2); // R^2 --> ratio al quadrato
		double delta = Math.sqrt(Math.pow(w / 2, 2) + Math.pow(h / 2, 2));
		// int iterations = 0;
		while (error > 0.01) {
			// iterations++;
			double ds = 0; // spostamento radiale apparente
			double dx = 0; // componente lungo x di ds
			double sf = 0.5f; // fattore di scala iniziale
			for (int i = 0; i < fpk; i++) {
				ds += k / sf;
				/*
				 * dx^2 = ds^2-dy^2 dx/dy = R
				 * 
				 * Soluzione
				 * 
				 * dx^2 = R^2ds^2/(1+R^2)
				 */
				// calcolo la componente lungo x dello spostamento
				dx = Math.sqrt(R2 * Math.pow(ds, 2) / (1 + R2));
				// calcolo il nuovo fattore di scala
				sf = (w + 2 * dx) / (2 * w);
				if (ds > delta)
					break;
			}
			error = Math.abs(ds - delta);
			if (ds > delta) {
				up_k = k;
				k = (k + down_k) / 2;
			} else {
				down_k = k;
				k = (k + up_k) / 2;
			}
		}
		// Log.getLogger().finer("Iterazioni: "+iterations);
		return k;
	}

	public static void main(String[] args) {
		int fpk = 104;
		long start = System.nanoTime();
		double k = FrameGenerator.computeK(360, 270, fpk);
		double pow_ratio = Math.pow(360d / 270d, 2);
		long end = System.nanoTime();
		System.out.println("k = " + k);
		float ds = 0;
		float dx = 0;
		float w = 360;
		float sf = 0.5f;
		for (int i = 0; i < 104; i++) {
			ds += k / sf;
			dx = (float) Math.sqrt(pow_ratio * Math.pow(ds, 2)
					/ (1 + pow_ratio));
			sf = (w + 2 * dx) / (2 * w);
		}
		System.out.println("dx dopo " + fpk + " iterazioni: " + dx);
		System.out.println("w/2 = " + w / 2);
		System.out.println("error = " + Math.abs(w / 2 - dx));
		System.out
				.println("Time = " + (float) (end - start) / 1000000 + " ms.");
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public int getNumFrames() {
		return frames.length;
	}

	public float getFps() {
		return fps;
	}

	public BufferedImage getFrame(int i, RenderingHints hints) {
		Graphics2D g = (Graphics2D) frameImage.getGraphics();
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// 0.6f));
		if (hints != null)
			g.setRenderingHints(hints);
		else
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		Frame f = frames[i];
		g.drawImage(f.keyReference, 0, 0, width, height, f.deltax, f.deltay,
				f.keyReference.getWidth() - f.deltax, f.keyReference
						.getHeight()
						- f.deltay, null);
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// 0.6f));
		// if(i>0 && i<frames.length-1){
		// f = frames[i+1];
		// g.drawImage(f.keyReference,0,0,width,height,f.deltax,f.deltay,f.keyReference.getWidth()-f.deltax,f.keyReference.getHeight()-f.deltay,null);
		// }
		// sovraimpressione
		g.setComposite(AlphaComposite
				.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		if (logo != null)
			g.drawImage(logo, 10, 10, null);
		// sovraimpressione titoli
		if (titles.size() > 0) {
			Iterator<Title> titlesIterator = titles.iterator();
			while (titlesIterator.hasNext()) {
				float currentTime = 1000 * i / this.fps;
				Title t = titlesIterator.next();
				if (t.getStartTime() < currentTime
						&& t.getEndTime() > currentTime) {
					g.setColor(t.getColor());
					g.setFont(t.getFont());
					g.drawString(t.getText(), t.getX(), t.getY());
				}
			}
		}
		return frameImage;
	}

	private class Frame {

		BufferedImage keyReference; // Immagine di riferimento
		int deltax;
		int deltay;

		Frame(BufferedImage keyReference, float dx) {
			super();
			this.keyReference = keyReference;
			// calcolo parametri
			float dy = dx / ratio;
			this.deltax = Math.round(dx);
			this.deltay = Math.round(dy);
		}

		// public BufferedImage getImage(RenderingHints hints){
		// //BufferedImage result = new
		// BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		// //Graphics2D g = result.createGraphics();
		// Graphics2D g = (Graphics2D)currentFrame.getGraphics();
		// // blurring
		// //BufferedImage img = (BufferedImage)op.filter(keyReference,null);
		// if(hints!=null) g.setRenderingHints(hints);
		// else
		// g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		// g.drawImage(keyReference,0,0,width,height,deltax,deltay,keyReference.getWidth()-deltax,keyReference.getHeight()-deltay,null);
		// //sovraimpressione
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// 0.6f));
		// if(logo!=null) g.drawImage(logo,10,10,null);
		// return currentFrame;
		// }

		// public BufferedImage getImage(Frame superImposed, AlphaComposite
		// alpha, RenderingHints hints){
		// // BufferedImage result = new
		// BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		// // Graphics2D g = result.createGraphics();
		// Graphics2D g = (Graphics2D)currentFrame.getGraphics();
		// if(hints!=null) g.setRenderingHints(hints);
		// else
		// g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		// g.drawImage(keyReference,0,0,width,height,deltax,deltay,keyReference.getWidth()-deltax,keyReference.getHeight()-deltay,null);
		// //sovraimpressione frame
		// g.setComposite(alpha);
		// g.drawImage(superImposed.getImage(hints),0,0,null);
		// // if(deltax>0){
		// // g.setComposite(alpha);
		// // g.drawImage(superImposed.getImage(hints),0,0,null);
		// // }
		// //sovraimpressione logo
		// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
		// 0.6f));
		// if(logo!=null) g.drawImage(logo,10,10,null);
		// return currentFrame;
		// }

	}

}
