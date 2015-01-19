package it.seat.visualzoom.player;

public interface IMoviePlayer {
	/** Costanti di direzione. */
	public static final int FORWARD = 1;
	public static final int BACKWARD = -1;

	/** Costanti di stato del player. */
	public static final int STOP = 0;
	public static final int PLAY = 1;

	public static final int STEP_BY_STEP = 4;

	public static final int FF = 2;
	public static final int REW = 3;

	/** Costanti di velocità. */
	// slow motion
	public static final float SPEED_X2 = 0.5f;
	public static final float SPEED_NORMAL = 1;
	public static final float SPEED_2X = 2;
	public static final float SPEED_4X = 4;
	public static final float SPEED_8X = 8;

	/** Costanti di qualità video. */
	public static final int QUALITY_LOW = 0;
	public static final int QUALITY_MEDIUM = 1;
	public static final int QUALITY_HIGH = 2;

	public abstract void play();

	public abstract void pause();

	public abstract void fastForward(float speed);

	public abstract void rewind(float speed);

	public abstract void fullRewind();

	public abstract void fullForward();

	public abstract void goToFrame(int frame);

	public abstract void setMouseControlEnabled(boolean enabled);

	public abstract void setKeyControlEnabled(boolean enabled);

	public abstract void setGridPainted(boolean painted);

	public abstract int getCurrentState();

	public abstract void destroy();
}