/*
 * Creato il 24-mag-2007
 */
package it.seat.visualzoom.zoom.gui.utils;

import java.awt.Font;

/**
 * @author Leonardo Landini
 */
public class FontManager {

	private static Font labelNormal;

	public static Font labelNormal() {
		if (labelNormal == null) {
			labelNormal = new Font("Verdana", java.awt.Font.PLAIN, 10);
		}
		return labelNormal;
	}

}
