/*
 * Creato il 20-giu-2007
 */
package it.seat.visualzoom.zoom.gui.event;

import it.seat.visualzoom.zoom.geocoding.Location;

/**
 * @author Leonardo Landini
 */
public interface LocationSelectListener {

	public void onLocationSelected(Location l);

	public void onLocationSelectCancelled();

}
