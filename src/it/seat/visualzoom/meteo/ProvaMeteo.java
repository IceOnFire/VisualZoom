package it.seat.visualzoom.meteo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.util.Iterator;
import java.util.List;

import meteo.bean.xml.Day;
import meteo.bean.xml.DayPart;
import meteo.bean.xml.Forecast;
import meteo.bean.xml.Location;
import proxy.MeteoService;

public class ProvaMeteo {
	public static void main(String[] args) {
		MeteoService mp = null;
		try {
			mp = MeteoService.getInstance();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mp.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"proxy.seat.it", 8080)));
		mp.setProxyUser("seatw2k\\d6556");
		mp.setProxyPass("c2VhdHcya1w2NTU2OnBhc3N3b3JkMSE=");

		float lon = 12.49f;
		float lat = 41.89f;
		int zEnd = 5;

		float minLon = lon - 0.2f;
		float minLat = lat - 0.2f;
		float maxLon = lon + 0.2f;
		float maxLat = lat + 0.2f;

		Location[] locations = null;
		try {
			locations = mp.getForecast(minLon, minLat, maxLon, maxLat, zEnd);//mp.getForecast(11.49353f, 40.89504f, 13.49353f, 42.89504f, 9);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * ogni location è costituita da 3 parti: details, forecast e realtime.
		 * A noi interessano solo i primi 2, se non solo il secondo
		 */
		for (int i=0; i<locations.length; i++) {
			/*
			 * gli unici dettagli disponibili sono il nome della città e la
			 * provincia
			 */
			System.out.println("Città: "
					+ locations[i].getDetails().getDetail("name"));
			System.out.println("Provincia: "
					+ locations[i].getDetails().getDetail("province"));

			/* info sulla geolocalizzazione: latitudine e longitudine */
			System.out.println("Latitudine: " + (float)locations[i].getLat()
					+ ", Longitudine: " + (float)locations[i].getLon());

			/* previsioni del tempo: sono suddivise in 5 giorni a partire da oggi */
			Forecast forecast = locations[i].getForecast();
			if (forecast != null) {
				Iterator<Day> it = forecast.daysIterator();
				while (it.hasNext()) {
					Day day = it.next();
					List<DayPart> dayParts = day.getParts();
					System.out.println("Meteo per il giorno " + day.getDate() + ":");
					for (DayPart dayPart : dayParts) {
						String nome = "";
						switch (dayPart.getPart()) {
						case DayPart.NOTTE:
							nome = "Ieri notte";
							break;
						case DayPart.MATTINA:
							nome = "Stamattina";
							break;
						case DayPart.POMERIGGIO:
							nome = "Questo pom";
							break;
						case DayPart.SERA:
							nome = "Staseeeera";
						}
						System.out.println("\t" + nome + ": " + dayPart.getWheather());
					}
					System.out.println();
				}
			}
			System.out.println("+--- ‾\\_(°•O)_/‾ ---+");
		}
	}
}
