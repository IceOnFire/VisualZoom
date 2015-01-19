package it.seat.visualzoom.meteo;

import it.seat.visualzoom.player.IMoviePlayer;
import it.seat.visualzoom.player.MoviePart;
import it.seat.visualzoom.player.effects.CyclicScaleEffect;
import it.seat.visualzoom.player.effects.FlipEffect;
import it.seat.visualzoom.player.effects.PositionScaleEffect;
import it.seat.visualzoom.player.effects.RotationEffect;
import it.seat.visualzoom.player.effects.TransparencyEffect;
import it.seat.visualzoom.player.layers.Layer;
import it.seat.visualzoom.player.layers.MapLayer;
import it.seat.visualzoom.player.layers.MeteoLayerFactory;
import meteo.bean.xml.Location;

public class MeteoMoviePartFactory {
	private static MeteoMoviePartFactory singleton;
	
	public static MeteoMoviePartFactory getInstance() {
		if (singleton == null) {
			singleton = new MeteoMoviePartFactory();
		}
		return singleton;
	}
	
	public MoviePart createZoomInMeteoMoviePart(int width, int height, float lon, float lat, int zStart, int zEnd, int length, Location[] locations, int day, int dayPart) {
		MoviePart meteoMoviePart = new MoviePart(length);

		// layer mappa che zooma in avanti
		MapLayer mapLayer = new MapLayer(width, height, zStart - zEnd + 1);
		mapLayer.addEffect(new CyclicScaleEffect(mapLayer, zStart, zEnd));
		mapLayer.initTextures(width, height, lon, lat, zStart, zEnd);
		meteoMoviePart.addLayer(mapLayer);
		
		// layer meteo che zoomano in avanti
		for (Location location : locations) {
			Layer meteoLayer = MeteoLayerFactory.getInstance().createMeteoLayer(lon, lat, zEnd, location, day, dayPart);
			if (meteoLayer != null) {
				meteoLayer.addEffect(new PositionScaleEffect(meteoLayer, zStart, zEnd));
				meteoLayer.addEffect(new TransparencyEffect(meteoLayer, IMoviePlayer.FORWARD));
				meteoMoviePart.addLayer(meteoLayer);
			}
		}
		
		return meteoMoviePart;
	}
	
	public MoviePart createStillMeteoMoviePart(int width, int height, float lon, float lat, int z, int length, Location[] locations, int day, int dayPart) {
		MoviePart meteoMoviePart = new MoviePart(length);

		// layer mappa senza zoom
		MapLayer mapLayer = new MapLayer(width, height, 1);
		mapLayer.initTextures(width, height, lon, lat, z, z);
		meteoMoviePart.addLayer(mapLayer);
		
		// layer meteo (senza zoom)
		for (Location location : locations) {
			Layer meteoLayer = MeteoLayerFactory.getInstance().createMeteoLayer(lon, lat, z, location, day, dayPart);
			if (meteoLayer != null) {
				meteoLayer.addEffect(new PositionScaleEffect(meteoLayer, 2));
				meteoMoviePart.addLayer(meteoLayer);
			}
		}
		
		return meteoMoviePart;
	}
	
	public MoviePart createStartFlippingMeteoMoviePart(int width, int height, float lon, float lat, int z, int length, Location[] locations, int day, int dayPart) {
		MoviePart meteoMoviePart = new MoviePart(length);

		// layer mappa senza zoom
		MapLayer mapLayer = new MapLayer(width, height, 1);
		mapLayer.initTextures(width, height, lon, lat, z, z);
		meteoMoviePart.addLayer(mapLayer);
		
		for (Location location : locations) {
			Layer meteoLayer = MeteoLayerFactory.getInstance().createMeteoLayer(lon, lat, z, location, day, dayPart);
			if (meteoLayer != null) {
//				int size = Math.max(meteoLayer.getWidth(), meteoLayer.getHeight());
//				meteoLayer.setZ(size);
				meteoLayer.addEffect(new PositionScaleEffect(meteoLayer, 2));
				meteoLayer.addEffect(new FlipEffect(meteoLayer, IMoviePlayer.FORWARD));
				meteoLayer.addEffect(new TransparencyEffect(meteoLayer, IMoviePlayer.BACKWARD));
				meteoMoviePart.addLayer(meteoLayer);
			}
		}
		return meteoMoviePart;
	}
	
	public MoviePart createEndFlippingMeteoMoviePart(int width, int height, float lon, float lat, int z, int length, Location[] locations, int day, int dayPart) {
		MoviePart meteoMoviePart = new MoviePart(length);

		// layer mappa senza zoom
		MapLayer mapLayer = new MapLayer(width, height, 1);
		mapLayer.initTextures(width, height, lon, lat, z, z);
		meteoMoviePart.addLayer(mapLayer);
		
		for (Location location : locations) {
			Layer meteoLayer = MeteoLayerFactory.getInstance().createMeteoLayer(lon, lat, z, location, day, dayPart);
			if (meteoLayer != null) {
//				int size = Math.max(meteoLayer.getWidth(), meteoLayer.getHeight());
//				meteoLayer.setZ(size);
				meteoLayer.addEffect(new RotationEffect(meteoLayer, RotationEffect.Y_AXIS, -90.0f));
				meteoLayer.addEffect(new PositionScaleEffect(meteoLayer, 2));
				meteoLayer.addEffect(new FlipEffect(meteoLayer, IMoviePlayer.FORWARD));
				meteoLayer.addEffect(new TransparencyEffect(meteoLayer, IMoviePlayer.FORWARD));
				meteoMoviePart.addLayer(meteoLayer);
			}
		}
		return meteoMoviePart;
	}
	
	public MoviePart createFlippingMeteoMoviePart(int width, int height, float lon, float lat, int z, int length, Location[] locations, int startDay, int startDayPart, int endDay, int endDayPart) {
		MoviePart meteoMoviePart = new MoviePart(length);

		// layer mappa senza zoom
		MapLayer mapLayer = new MapLayer(width, height, 1);
		mapLayer.initTextures(width, height, lon, lat, z, z);
		meteoMoviePart.addLayer(mapLayer);
		
		// layer meteo (senza zoom)
//		for (Location location : locations) {
//			Layer meteoLayer = MeteoLayerFactory.getInstance().createDoubleFacedMeteoLayer(lon, lat, z, location, startDay, startDayPart, endDay, endDayPart);
//			if (meteoLayer != null) {
//				meteoLayer.addEffect(new PositionScaleEffect(meteoLayer, 2));
//				meteoLayer.addEffect(new FlipEffect(meteoLayer, IMoviePlayer.FORWARD));
//				meteoMoviePart.addLayer(meteoLayer);
//			}
//		}
		
		for (Location location : locations) {
			Layer startMeteoLayer = MeteoLayerFactory.getInstance().createMeteoLayer(lon, lat, z, location, startDay, startDayPart);
			if (startMeteoLayer != null) {
				int size = Math.max(startMeteoLayer.getWidth(), startMeteoLayer.getHeight());
				startMeteoLayer.setZ(2*size);
				startMeteoLayer.addEffect(new PositionScaleEffect(startMeteoLayer, 2));
				startMeteoLayer.addEffect(new FlipEffect(startMeteoLayer, IMoviePlayer.FORWARD));
				startMeteoLayer.addEffect(new TransparencyEffect(startMeteoLayer, IMoviePlayer.BACKWARD));
				meteoMoviePart.addLayer(startMeteoLayer);
			}
			
			Layer endMeteoLayer = MeteoLayerFactory.getInstance().createMeteoLayer(lon, lat, z, location, endDay, endDayPart);
			if (endMeteoLayer != null) {
				int size = Math.max(endMeteoLayer.getWidth(), endMeteoLayer.getHeight());
				endMeteoLayer.setZ(size);
				endMeteoLayer.addEffect(new RotationEffect(endMeteoLayer, RotationEffect.Y_AXIS, 180.0f));
				endMeteoLayer.addEffect(new PositionScaleEffect(endMeteoLayer, 2));
				endMeteoLayer.addEffect(new FlipEffect(endMeteoLayer, IMoviePlayer.FORWARD));
				endMeteoLayer.addEffect(new TransparencyEffect(endMeteoLayer, IMoviePlayer.FORWARD));
				meteoMoviePart.addLayer(endMeteoLayer);
			}
		}
		
		return meteoMoviePart;
	}
	
	public MoviePart createZoomOutMeteoMoviePart(int width, int height, float lon, float lat, int zStart, int zEnd, int length, Location[] locations, int day, int dayPart) {
		MoviePart meteoMoviePart = new MoviePart(length);

		// layer mappa che zooma all'indietro
		MapLayer mapLayer = new MapLayer(width, height, zEnd - zStart + 1);
		mapLayer.addEffect(new CyclicScaleEffect(mapLayer, zStart, zEnd));
		mapLayer.initTextures(width, height, lon, lat, zStart, zEnd);
		meteoMoviePart.addLayer(mapLayer);
		
		// layer meteo che zoomano all'indietro
		for (Location location : locations) {
			Layer meteoLayer = MeteoLayerFactory.getInstance().createMeteoLayer(lon, lat, zStart, location, day, dayPart);
			if (meteoLayer != null) {
				meteoLayer.addEffect(new PositionScaleEffect(meteoLayer, zStart, zEnd));
				meteoLayer.addEffect(new TransparencyEffect(meteoLayer, IMoviePlayer.BACKWARD));
				meteoMoviePart.addLayer(meteoLayer);
			}
		}
		
		return meteoMoviePart;
	}
}
