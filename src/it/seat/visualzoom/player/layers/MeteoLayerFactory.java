package it.seat.visualzoom.player.layers;

import it.seat.visual.model.coords.LL;
import it.seat.visual.model.coords.XY;
import it.seat.visualzoom.player.IconConstants;
import it.seat.visualzoom.zoom.utils.ProxyPrefs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import meteo.bean.xml.Day;
import meteo.bean.xml.DayPart;
import meteo.bean.xml.Forecast;
import meteo.bean.xml.Location;
import proxy.MeteoService;

public class MeteoLayerFactory {
	private static MeteoLayerFactory singleton;

	public static MeteoLayerFactory getInstance() {
		if (singleton == null) {
			singleton = new MeteoLayerFactory();
		}
		return singleton;
	}

	public Location[] getLocations(double clon, double clat, int width,
			int height, int zoomLevel) {
		MeteoService mp = null;
		try {
			mp = MeteoService.getInstance();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.setProxy(ProxyPrefs.getInstance().getSelectedProxy());

		LL centerNavtech = new LL((float) clon, (float) clat);
		XY minXY = new XY(-(width >> 1), -(height >> 1));
		XY maxXY = new XY(width >> 1, height >> 1);
		LL minNavtech = minXY.toLL(centerNavtech, zoomLevel, 2);
		LL maxNavtech = maxXY.toLL(centerNavtech, zoomLevel, 2);

		Location[] locations = null;
		try {
			locations = mp.getForecast(minNavtech.getLon(),
					minNavtech.getLat(), maxNavtech.getLon(), maxNavtech
							.getLat(), zoomLevel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return locations;
	}

	public Layer createDoubleFacedMeteoLayer(float clon, float clat, int zoomLevel,
			Location location, int startDay, int startDayPart, int endDay,
			int endDayPart) {
		Forecast forecast = location.getForecast();
		Day day = forecast.getDay(startDay);
		DayPart dayPart = (DayPart) day.getParts().get(startDayPart);
		BufferedImage image = getWeatherIcon(dayPart.getPart(), dayPart
				.getWheather());

		Day backDay = forecast.getDay(endDay);
		DayPart backDayPart = (DayPart) backDay.getParts().get(endDayPart);
		BufferedImage backImage = getWeatherIcon(backDayPart.getPart(),
				backDayPart.getWheather());

		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.BLUE);
		g2d.setFont(new Font("Arial", Font.PLAIN, 12));
		g2d.drawString(location.getDetails().getDetail("name"), 0, image
				.getHeight() / 2);
		g2d.dispose();

		Graphics2D bg2d = backImage.createGraphics();
		bg2d.setColor(Color.BLUE);
		bg2d.setFont(new Font("Arial", Font.PLAIN, 12));
		bg2d.drawString(location.getDetails().getDetail("name"), 0, backImage
				.getHeight() / 2);
		bg2d.dispose();

		LL locationNavtech = new LL((float) location.getLon(), (float) location
				.getLat());
		LL centerNavtech = new LL((float) clon, (float) clat);
		XY delta = locationNavtech.toDelta(centerNavtech, zoomLevel, 2);
		int dx = delta.getX();
		int dy = delta.getY();

		DoubleFacedSquareLayer layer = new DoubleFacedSquareLayer(dx, dy);
		layer.setImage(image);
		layer.setBackImage(backImage);
		int size = Math.max(layer.getWidth(), layer.getHeight());
		layer.setZ(size / 2);

		return layer;
	}

	public Layer createMeteoLayer(float clon, float clat, int zoomLevel,
			Location location, int day, int dayPart) {
		Forecast forecast = location.getForecast();
		if (forecast == null) {
			return null;
		}
		Day currentDay = forecast.getDay(day);
		DayPart currentDayPart = (DayPart) currentDay.getParts().get(dayPart);
		BufferedImage image = getWeatherIcon(currentDayPart.getPart(),
				currentDayPart.getWheather());

		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.BLUE);
		g2d.setFont(new Font("Arial", Font.PLAIN, 12));
		g2d.drawString(location.getDetails().getDetail("name"), 0, image
				.getHeight() / 2);
		g2d.dispose();

		LL locationNavtech = new LL((float) location.getLon(), (float) location
				.getLat());
		LL centerNavtech = new LL((float) clon, (float) clat);
		XY delta = locationNavtech.toDelta(centerNavtech, zoomLevel, 2);
		int dx = delta.getX();
		int dy = delta.getY();

		Layer layer = new SquareLayer(dx, dy);
		layer.setImage(image);
		layer.setWidth(layer.getWidth() * 2);
		layer.setHeight(layer.getHeight() * 2);
		int size = Math.max(layer.getWidth(), layer.getHeight());
		layer.setZ(size / 2);

		return layer;
	}

	private BufferedImage getWeatherIcon(int dayPart, int weather) {
		String path = null;
		// System.out.println(weather);
		switch (weather) {
		case 1:
			if (dayPart == 0) {
				path = IconConstants.LUNA;
			} else {
				path = IconConstants.SOLE;
			}
			break;
		case 3:
			if (dayPart == 0) {
				path = IconConstants.LUNA_NUVOLETTA;
			} else {
				path = IconConstants.SOLE_NUVOLETTA;
			}
			break;
		case 4:
			if (dayPart == 0) {
				path = IconConstants.LUNA_NUVOLA1;
			} else {
				path = IconConstants.SOLE_NUVOLA1;
			}
			break;
		case 5:
			if (dayPart == 0) {
				path = IconConstants.LUNA_PIOGGIA;
			} else {
				path = IconConstants.SOLE_PIOGGIA;
			}
			break;
		case 8:
			path = IconConstants.NUVOLA1;
			break;
		case 9:
			path = IconConstants.PIOGGIA;
			break;
		case 10:
			path = IconConstants.PIOGGE;// anche di più!
			break;
		case 11:
			path = IconConstants.NEVE;
			break;
		case 12:
			path = IconConstants.NEVISCHIO;
			break;
		case 13:
			path = IconConstants.FULMINE;
			break;
		case 14:
			path = IconConstants.NEBBIA;
			break;
		default:
			path = IconConstants.BOH;
			System.out.println("Codice meteo sconosciuto: " + weather);
		}
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}
}
