package it.seat.visualzoom.player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MoviePlayerController implements KeyListener,
		MouseListener, MouseMotionListener, MouseWheelListener {
	private MoviePlayer player;
	private int prevMouseX, prevMouseY;
	
	public MoviePlayerController(MoviePlayer player) {
		this.player = player;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		prevMouseX = e.getX();
		prevMouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		float thetaX = (prevMouseY - y) % 360;
		float thetaY = (prevMouseX - x) % 360;

		prevMouseX = x;
		prevMouseY = y;

		player.updateRotation(thetaX, thetaY, 0);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		player.skipFrames(e.getWheelRotation(), IMoviePlayer.BACKWARD);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			player.rewind(IMoviePlayer.SPEED_4X);
			break;
		case KeyEvent.VK_RIGHT:
			player.fastForward(IMoviePlayer.SPEED_4X);
			break;
		case KeyEvent.VK_DOWN:
			player.fullRewind();
			break;
		case KeyEvent.VK_UP:
			player.fullForward();
			break;
		case KeyEvent.VK_MINUS:
			player.skipFrames(1, IMoviePlayer.BACKWARD);
			break;
		case KeyEvent.VK_PLUS:
			player.skipFrames(1, IMoviePlayer.FORWARD);
			break;
		case KeyEvent.VK_SPACE:
			if (player.getState() == IMoviePlayer.STOP || player.getState() == IMoviePlayer.STEP_BY_STEP) {
				player.play();
			} else {
				player.pause();
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			player.pause();
			break;
		case KeyEvent.VK_RIGHT:
			player.pause();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
