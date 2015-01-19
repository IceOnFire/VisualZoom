/*
 * Creato il 10-apr-2007
 */
package it.seat.visualzoom.zoom.geocoding;

/**
 * @author Leonardo Landini
 */
public class GeocodingException extends Exception {

	/**
	 * @param message
	 */
	public GeocodingException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GeocodingException(String message, Throwable cause) {
		super(message, cause);
	}
}
