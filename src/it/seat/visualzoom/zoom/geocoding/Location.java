/*
 * Creato il 10-apr-2007
 */
package it.seat.visualzoom.zoom.geocoding;

/**
 * @author Leonardo Landini
 */
public class Location {

	String com;
	String fraz;
	String prov;
	String codCom;
	String codFraz;
	String address;
	String civ;
	float lat;
	float lon;

	/**
	 * @param com
	 * @param fraz
	 * @param prov
	 * @param codCom
	 * @param codFraz
	 * @param address
	 * @param civ
	 * @param lat
	 * @param lon
	 */
	public Location(String com, String fraz, String prov, String codCom,
			String codFraz, String address, String civ, float lat, float lon) {
		super();
		this.com = com;
		this.fraz = fraz;
		this.prov = prov;
		this.codCom = codCom;
		this.codFraz = codFraz;
		this.address = address;
		this.civ = civ;
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * @param com
	 * @param prov
	 * @param codCom
	 * @param codFraz
	 * @param lat
	 * @param lon
	 */
	public Location(String com, String fraz, String prov, String codCom,
			String codFraz, float lat, float lon) {
		this(com, fraz, prov, codCom, codFraz, "", "", lat, lon);
	}

	public Location(String com, String fraz, String prov, String codCom,
			String codFraz, String address, String civ) {
		this(com, fraz, prov, codCom, codFraz, address, civ, 0, 0);
	}

	/**
	 * @param com
	 * @param prov
	 * @param codCom
	 * @param codFraz
	 */
	public Location(String com, String fraz, String prov, String codCom,
			String codFraz) {
		this(com, fraz, prov, codCom, codFraz, 0, 0);
	}

	public Location(float lat, float lon) {
		this("", "", "", "", "", lat, lon);
	}

	public String toString() {
		return "Location [com=" + com + ", fraz=" + fraz + ", prov=" + prov
				+ ", codCom=" + codCom + ", codFraz=" + codFraz + ", lat="
				+ lat + ", long=" + lon + "]";
	}

	public String getCodCom() {
		return codCom;
	}

	public String getCodFraz() {
		return codFraz;
	}

	public String getCom() {
		return com;
	}

	public String getFraz() {
		return fraz;
	}

	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}

	public String getProv() {
		return prov;
	}

	public String getAddress() {
		return address;
	}

	public String getCiv() {
		return civ;
	}
}
