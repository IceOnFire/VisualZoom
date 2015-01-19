package it.seat.visualzoom.player.effects;

import it.seat.visualzoom.player.layers.MapLayer;


public class CyclicScaleEffect extends ScaleEffect {
	private int zoomLevel;
	
	public CyclicScaleEffect(MapLayer layer, int zStart, int zEnd) {
		super(layer, zStart, zEnd);
		
		zoomLevel = zStart;
	}
	
	@Override
	public void update(int time, int length) {
		int direction = new Integer(zStart).compareTo(zEnd);
		int zoomLevels = Math.abs(zStart - zEnd);
		float zoomLength = 1f * length / zoomLevels;
		MapLayer mapLayer = (MapLayer)layer;
		/* lo scalingFactor è funzione esponenziale del tempo */
		if (direction > 0) {
			scalingFactor = (float) Math.pow(2, (time % zoomLength) / zoomLength);
			zoomLevel = (int) Math.floor(time / zoomLength) + zEnd;
			mapLayer.setTexture(mapLayer.getTexture(zoomLevel - zEnd));
		} else if (direction < 0) {
			scalingFactor = (float) Math.pow(2, 1 - (time % zoomLength) / zoomLength);
			zoomLevel = (int) Math.ceil((zoomLevels - 1) - time / zoomLength) + zStart;
			mapLayer.setTexture(mapLayer.getTexture(zoomLevel - zStart));
		}
	}
}
