package io.rudin.minetest.tileserver.util;

public class CoordinateResolver {

	public static final int TILE_PIXEL_SIZE = 256;
	public static final int MAPBLOCK_PIXEL_SIZE = 16;
	
	public static final int MAX_ZOOM = 13;
	public static final int MIN_ZOOM = 1;
	public static final int ONE_TO_ONE_ZOOM = 13; // 1 tile == 1 mapblock
	
	public static class TileInfo {
		public int x, y;
		public int zoom;
		public double width, height; //in tiles

		public TileInfo toZoom(int zoom) {
			TileInfo info = new TileInfo();
			
			double deltaFactor = Math.pow(2, zoom - this.zoom);
			
			info.zoom = zoom;
			//info.x = (int)(this.x * deltaFactor) + (int)((this.x * deltaFactor) % 2);
			//info.y = (int)(this.y * deltaFactor) + (int)((this.y * deltaFactor) % 2);
			info.x = (int)Math.floor((double)this.x * deltaFactor);
			info.y = (int)Math.floor((double)this.y * deltaFactor);

			info.height = this.height / deltaFactor;
			info.width = this.width / deltaFactor;
			
			
			return info;
		}
	}

	public static TileInfo fromCoordinates(int x, int z) {
		TileInfo info = new TileInfo();

		info.zoom = ONE_TO_ONE_ZOOM;

		info.x = x;
		info.y = z * -1;
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
	 * 9 == 1 (16x16 mapblocks)
	 * 10 == 2 (8x8 mapblocks)
	 * 11 == 4 (4x4 mapblocks)
	 * 12 == 8 (2x2 mapblocks)
	 * 13 == 16 (1 mapblock)
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

		double factor  = Math.pow(2, ONE_TO_ONE_ZOOM - zoom);
		
		// tile with 1:1 map resolution
		info.x = (int)(x * factor);
		info.z = (int)(y * factor * -1);
		info.height = factor;
		info.width = factor;


		return info;
	}
	
}
