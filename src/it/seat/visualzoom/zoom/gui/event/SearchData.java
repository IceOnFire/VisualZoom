/*
 * Creato il 8-mag-2007
 */
package it.seat.visualzoom.zoom.gui.event;

/**
 * @author Leonardo Landini
 */
public class SearchData {

	private String comune;
	private String provincia;
	private String indirizzo;
	private String civico;
	private int duration;

	/**
	 * @param comune
	 * @param provincia
	 * @param indirizzo
	 * @param civico
	 * @param duration
	 */
	public SearchData(String comune, String provincia, String indirizzo,
			String civico, int duration) {
		super();
		this.comune = comune;
		this.provincia = provincia;
		this.indirizzo = indirizzo;
		this.civico = civico;
		this.duration = duration;
	}

	public String getCivico() {
		return civico;
	}

	public String getComune() {
		return comune;
	}

	public int getDuration() {
		return duration;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public String getProvincia() {
		return provincia;
	}
}
