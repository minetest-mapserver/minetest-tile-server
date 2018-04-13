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
		public double width, height; //in map-blocks
	}
	
	/*
	 * ...
	 * 7 == 0.25
	 * 8 == 0.5
	 * 9 == 1
	 * 10 == 2
	 * 11 == 4
	 * 12 == 8
	 * ...
	 */
	public static double getZoomFactor(int zoom) {
		return Math.pow(2, zoom - 9);
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
		info.height = 16;
		info.width = 16;

		
		if (zoom < 9) {
			//zoomed out
			
			int factor = (int) Math.pow(2, 9 - zoom);

			info.x /= factor;
			info.z /= factor;
			
			info.height *= factor;
			info.width *= factor;
			
		} else if (zoom > 9) {
			//zoomed in
			
			int factor = (int) Math.pow(2, zoom - 9);
			info.x *= factor;
			info.z *= factor;

			
			info.height /= factor;
			info.width /= factor;

		} else {
			//zoom == 9
			//Nothing to do...
		}
		
		return info;
	}
	
}
