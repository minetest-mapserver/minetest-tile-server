package io.rudin.minetest.util;

public class CoordinateResolver {

	public static final int TILE_PIXEL_SIZE = 256;
	public static final int MAPBLOCK_PIXEL_SIZE = 16;
	
	public static class TileInfo {
		public int x, y;
	}
	
	public static TileInfo fromCoordinates(int x, int z) {
		TileInfo info = new TileInfo();
		
		return info;
	}
	
	public static class MapBlockCoordinateInfo {
		public int x, z;
	}
	
	public static MapBlockCoordinateInfo fromTile(int x, int y, int zoom) {
		MapBlockCoordinateInfo info = new MapBlockCoordinateInfo();
		
		/*
		 * Leaflet:
		 *  Y
		 * X ->  ||
		 *       \/
		 * 
		 * Minetest:
		 *  Z
		 * X ->  /\
		 *       ||
		 * 
		 */
		
		// tile with 1:1 map resolution
		info.x = x * 16;
		info.z = y * 16 * -1;
		
		if (zoom < 9) {
			//zoomed out
			//TODO
			
		} else if (zoom > 9) {
			//zoomed in
			//TODO
		}
		
		return info;
	}
	
}
