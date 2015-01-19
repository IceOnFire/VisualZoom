package it.seat.visualzoom.panorama;

import it.seat.visualzoom.player.IMoviePlayer;
import it.seat.visualzoom.player.Movie;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.Screenshot;
import com.sun.opengl.util.j2d.Overlay;

/**
 * La classe MoviePlayer è un componente Swing sul quale viene rappresentata
 * un'animazione 3D. E' possibile impostare vari valori, fra i quali le
 * dimensioni della scena, gli istanti iniziale e finale, il numero di frame al
 * secondo e i livelli di zoom consentiti.
 * 
 * @author Antony Mistretta
 */
public class MoviePlayer extends GLCanvas implements IMoviePlayer,
		GLEventListener, KeyListener, MouseListener, MouseMotionListener,
		MouseWheelListener {
	private static final long serialVersionUID = 1L;

	private Animator animator;

	/** Attributi di questo player. */
	/** Il filmato da rappresentare. */
	private Movie movie;
	/** Oggetti utili alla visualizzazione di layer fissi su schermo. */
	private Overlay overlay;
//	private TextRenderer renderer;

	/** Attributi utili per la rotazione. */
	private int prevMouseX, prevMouseY;
	private float posx = 0, posy = 0, posz = 0;
	private float view_rotx = 90/* 41 */, view_roty = 0, view_rotz = 0;

	/** Direzione di moto (-1=indietro, 1=avanti). */
	private int direction;
	/** Velocità dell'animazione, in modulo. */
	private float speed;
	/** Stato corrente (play, pause, step-by-step). */
	private int state;

	/** Determina se il player è in modalità capture. */
	private boolean captureMode;
	/** Foto corrente scattata. */
	private BufferedImage screenshot;
	
	public MoviePlayer(Movie movie, int width, int height) {
		super();

		this.movie = movie;

//		setGL(new DebugGL(getGL()));
		setFocusable(true);
		setSize(width, height);
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addGLEventListener(this);

		animator = new FPSAnimator(this, movie.getFrameRate());
		setCaptureMode(false);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		gl.setSwapInterval(1);

		gl.glClearColor(0.647058824f, 0.839215686f, 1.0f, 1.0f);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_TEXTURE_2D);

		gl.glEnable(GL.GL_NORMALIZE);

		/* abilita la trasparenza nelle texture */
		gl.glEnable(GL.GL_BLEND);
		// do not draw the transparent parts of the texture
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// don't show source alpha parts in the destination
		// determine which areas of the polygon are to be rendered
		gl.glEnable(GL.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0); // only render if alpha > 0
		
		/* inizializza il filmato */
		movie.init(drawable);

//		renderer = new TextRenderer(new Font("Arial", Font.PLAIN, 12), true,
//				false);
        
		/* si posiziona all'inizio del filmato */
		goToFrame(0);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL gl = drawable.getGL();

		float m = 1f * height / width;

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		
		gl.glFrustum(-0.5, 0.5, -m/2, m/2, 1, 4);
		new GLU().gluLookAt(0, 0, 0, 0, 0, 1, 0, 1, 0);
		
		/* ribalta la scena lungo l'asse y */
		gl.glScalef(-1, 1, 1);
		/* sposta la visuale a 2 passi dall'origine */
		gl.glTranslatef(0, 0, 2);
		
		/* normalizza la scena tra -0.5 e 0.5 */
//		float aspectRatio = movie.getAspectRatio();
//		gl.glScalef(2f/movie.getWidth(), 2f/movie.getHeight()*aspectRatio, 2f/movie.getWidth());
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		/* ruota tutta la visuale */
		// il doppio translate sposta l'asse di rotazione
		// sul bordo superiore o inferiore della mappa
		float aspectRatio = movie.getAspectRatio();
		gl.glTranslatef(0, aspectRatio, 0);
		gl.glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
		gl.glTranslatef(0, -aspectRatio, 0);
		// ruota anche attorno all'asse z?
		gl.glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);
		
//		/* normalizza la scena tra -0.5 e 0.5 */
		gl.glScalef(2f/movie.getWidth(), 2f/movie.getHeight()*aspectRatio, 2f/movie.getWidth());
//		
		/* disegna il filmato */
		movie.display(drawable);

		/* anima la scena */
		if (state == PLAY) {
			movie.updateCurrentTime((int) (direction * speed * 1000f / movie.getFrameRate()));
//			takeScreenshot(drawable);
		} else if (state == STEP_BY_STEP) {
			takeScreenshot(drawable);
//			changeState(STOP);
		}
	}

	public int getState() {
		return state;
	}

	public void updateRotation(float thetaX, float thetaY, float thetaZ) {
		view_rotx += thetaX;
		view_rotz += thetaY;
	}

	/**
	 * Aggiorna gli attributi degli oggetti presenti nella scena in funzione del
	 * tempo trascorso.
	 */
	private void takeScreenshot(GLAutoDrawable drawable) {
		/* scatta una foto, se richiesto */
		// lo screenshot viene eseguito sul frame precedente, pertanto meglio
		// evitarlo in 0
		if (captureMode && movie.getCurrentFrameIndex() > 0) {
			try {
				// scatta una foto e la salva su BufferedImage
				screenshot = Screenshot.readToBufferedImage(getWidth(),
						getHeight());
			} catch (GLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setCaptureMode(boolean captureMode) {
		this.captureMode = captureMode;
		if (captureMode) {
			animator.stop();
		} else {
			animator.start();
		}
	}

	public BufferedImage getScreenshot() {
		return screenshot;
	}

	@Override
	public void pause() {
		changeState(STOP);
		System.out.println("Pause");
	}

	@Override
	public void play() {
		direction = FORWARD;
		speed = SPEED_NORMAL;
		changeState(PLAY);
		System.out.println("Play");
	}

	@Override
	public void rewind(float speed) {
		direction = BACKWARD;
		this.speed = speed;
		changeState(PLAY);
		System.out.println("Rewind");
	}

	@Override
	public void fullRewind() {
		movie.setCurrentTime(0);
		changeState(STEP_BY_STEP);
		System.out.println("Full rewind");
	}

	@Override
	public void fastForward(float speed) {
		direction = FORWARD;
		this.speed = speed;
		changeState(PLAY);
		System.out.println("Fast forward");
	}

	@Override
	public void fullForward() {
		movie.setCurrentTime(movie.getLength());
		changeState(STEP_BY_STEP);
		System.out.println("Full forward");
	}

	@Override
	public void goToFrame(int frame) {
		movie.goToFrame(frame);
		changeState(STEP_BY_STEP);
		System.out.println("Step");
		if (captureMode) {
			display();
		}
	}
	
	public void skipFrames(int nFrames, int direction) {
		goToFrame(movie.getCurrentFrameIndex() + nFrames*direction);
	}

	@Override
	public void setGridPainted(boolean painted) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setKeyControlEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMouseControlEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	public int getCurrentState() {
		return state;
	}

	public void changeState(int newState) {
		state = newState;
	}
	
	public Movie getMovie() {
		return movie;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		prevMouseX = e.getX();
		prevMouseY = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		float thetaX = (prevMouseY - y) % 360;
		float thetaY = (prevMouseX - x) % 360;

		prevMouseX = x;
		prevMouseY = y;

		view_rotx += thetaX;
		view_rotz += thetaY;
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		movie.updateCurrentTime(-20 * e.getWheelRotation());
		changeState(STEP_BY_STEP);
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			view_rotz -= 2;
			break;
		case KeyEvent.VK_RIGHT:
			view_rotz += 2;
			break;
		case KeyEvent.VK_DOWN:
			view_rotx -= 2;
			break;
		case KeyEvent.VK_UP:
			view_rotx += 2;
			break;
		case KeyEvent.VK_MINUS:
			goToFrame(movie.getCurrentFrameIndex() - 1);
			break;
		case KeyEvent.VK_PLUS:
			goToFrame(movie.getCurrentFrameIndex() + 1);
			break;
		case KeyEvent.VK_SPACE:
			if (state == STOP) {
				play();
			} else {
				pause();
			}
			break;
		case KeyEvent.VK_A:
			posx += 0.2f;
			break;
		case KeyEvent.VK_D:
			posx -= 0.2f;
			break;
		case KeyEvent.VK_W:
			posz -= 0.2f;
			break;
		case KeyEvent.VK_S:
			posz += 0.2f;
		}
	}

	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			pause();
			break;
		case KeyEvent.VK_RIGHT:
			pause();
			break;
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
