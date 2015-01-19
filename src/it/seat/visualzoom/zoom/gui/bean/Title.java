package it.seat.visualzoom.zoom.gui.bean;

import java.awt.Color;
import java.awt.Font;

public class Title {

	private String text;
	private Font font;
	private Color color;

	private int x, y;
	private long startTime;
	private long endTime;

	public Title(String text, Font font, Color color) {
		super();
		this.text = text;
		this.font = font;
		this.color = color;
		this.startTime = Long.MIN_VALUE;
		this.endTime = Long.MAX_VALUE;
		this.x = 0;
		this.y = 0;
	}

	public Color getColor() {
		return color;
	}

	public Font getFont() {
		return font;
	}

	public String getText() {
		return text;
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

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public String toString() {
		return text;
	}

}
