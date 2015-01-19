/*
 * Creato il 18-giu-2007
 */
package it.seat.visualzoom.zoom.gui.event;

import it.seat.visualzoom.zoom.geocoding.Location;

/**
 * @author Leonardo Landini
 */
public interface NewProjectDialogListener {

	public void onNewProjectConfirm(Location l, int width, int height,
			int z_start, int z_end, int length);

	public void onNewProjectCancel();
}
