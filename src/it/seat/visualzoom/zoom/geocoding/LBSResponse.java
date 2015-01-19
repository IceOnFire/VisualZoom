/*
 * Creato il 23-mag-2007
 */
package it.seat.visualzoom.zoom.geocoding;

/**
 * @author Leo
 */
public class LBSResponse {

	public static final int TYPE_OK = 0;
	public static final int TYPE_COMUNE_NON_UNIVOCO = 10;
	public static final int TYPE_INDIRIZZO_NON_UNIVOCO = 2;
	public static final int TYPE_NO_RESULT = -1;

	private int type;
	private Location[] locations;

	/**
	 * @param type
	 * @param locations
	 */
	private LBSResponse(int type, Location[] locations) {
		super();
		this.type = type;
		this.locations = locations;
	}

	public Location[] getLocations() {
		return locations;
	}

	public int getType() {
		return type;
	}

	// static
	public static LBSResponse newResponseOK(Location[] locations) {
		return new LBSResponse(TYPE_OK, locations);
	}

	public static LBSResponse newResponseComuneNonUnivoco(Location[] locations) {
		return new LBSResponse(TYPE_COMUNE_NON_UNIVOCO, locations);
	}

	public static LBSResponse newResponseIndirizzoNonUnivoco(
			Location[] locations) {
		return new LBSResponse(TYPE_INDIRIZZO_NON_UNIVOCO, locations);
	}

	public static LBSResponse newResponseNessunRisultato() {
		return new LBSResponse(TYPE_NO_RESULT, new Location[0]);
	}
}
