package io.rudin.minetest.tileserver.util;

public class CoordinateResolver {

	public static final int TILE_PIXEL_SIZE = 256;
	public static final int MAPBLOCK_PIXEL_SIZE = 16;
	
	public static final int MAX_ZOOM = 13;
	public static final int MIN_ZOOM = 1;
	public static final int DEFAULT_ZOOM = 9;
	
	public static class TileInfo {
		public int x, y;
		public int zoom;
		public double width, height; //in tiles

		public TileInfo toZoom(int zoom) {
			TileInfo info = new TileInfo();
			
			double deltaFactor = Math.pow(2, zoom - this.zoom);
			
			info.zoom = zoom;
			info.x = (int)(this.x * deltaFactor) + (int)((this.x * deltaFactor) % 2);
			info.y = (int)(this.y * deltaFactor) + (int)((this.y * deltaFactor) % 2);
			info.height = this.height / deltaFactor;
			info.width = this.width / deltaFactor;
			
			
			return info;
		}
	}
	
	public static TileInfo fromCoordinates(int x, int z) {
		TileInfo info = new TileInfo();
		
		info.zoom = DEFAULT_ZOOM;

		info.x = x / 16;
		info.y = z / 16 * -1;
		info.height = 1;
		info.width = 1;
		
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
		return Math.pow(2, zoom - DEFAULT_ZOOM);
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

		
		if (zoom < DEFAULT_ZOOM) {
			//zoomed out
			
			int factor = (int) Math.pow(2, DEFAULT_ZOOM - zoom);

			info.x /= factor;
			info.z /= factor;
			
			info.height *= factor;
			info.width *= factor;
			
		} else if (zoom > DEFAULT_ZOOM) {
			//zoomed in
			
			int factor = (int) Math.pow(2, zoom - DEFAULT_ZOOM);
			info.x *= factor;
			info.z *= factor;

			
			info.height /= factor;
			info.width /= factor;

		} else {
			//zoom == 9 (DEFAULT_ZOOM)
			//Nothing to do...
		}
		
		return info;
	}
	
}
