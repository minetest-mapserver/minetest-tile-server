package io.rudin.minetest.tileserver.util;

public class CoordinateResolver {

	public static final int TILE_PIXEL_SIZE = 256;
	public static final int MAPBLOCK_PIXEL_SIZE = 16;
	
	public static final int MAX_ZOOM = 13;
	public static final int MIN_ZOOM = 1;
	public static final int ONE_TO_ONE_ZOOM = 13; // 1 tile == 1 mapblock

	/**
	 * Tile information
	 */
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

		@Override
		public String toString() {
			return "TileInfo{" +
					"x=" + x +
					", y=" + y +
					", zoom=" + zoom +
					", width=" + width +
					", height=" + height +
					'}';
		}
	}

	/**
	 * Get tile coordinates from mapblock coords
	 * @param x
	 * @param z
	 * @return
	 */
	public static TileInfo fromCoordinates(int x, int z) {
		TileInfo info = new TileInfo();

		info.zoom = ONE_TO_ONE_ZOOM;

		info.x = x;
		info.y = (z * -1) + 1;
		info.height = 1;
		info.width = 1;

		return info;
	}

	public static class MapBlockCoordinateInfo {
		public int x, z;

		@Deprecated //area
		public double width, height; //in map-blocks

		@Override
		public String toString() {
			return "MapBlockCoordinateInfo{" +
					"x=" + x +
					", z=" + z +
					", width=" + width +
					", height=" + height +
					'}';
		}
	}

	/**
	 * An area defined by two mapblock "points"
	 */
	public static class MapBlockArea {
		public MapBlockCoordinateInfo pos1, pos2;
	}


	public static MapBlockArea getMapBlockArea(int x, int y, int zoom){
		MapBlockArea area = new MapBlockArea();

		MapBlockCoordinateInfo pos1 = new MapBlockCoordinateInfo();
		MapBlockCoordinateInfo pos2 = new MapBlockCoordinateInfo();

		double factor  = Math.pow(2, ONE_TO_ONE_ZOOM - zoom);

		pos1.x = (int)(x * factor);
		pos1.z = (int)((y-1) * factor * -1);

		//TODOs

		area.pos1 = pos1;
		area.pos2 = pos2;

		return area;
	}

	@Deprecated
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
		info.z = (int)((y-1) * factor * -1);
		info.height = factor;
		info.width = factor;


		return info;
	}
	
}
