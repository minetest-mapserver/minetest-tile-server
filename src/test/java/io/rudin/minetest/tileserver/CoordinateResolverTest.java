package io.rudin.minetest.tileserver;

import org.junit.Assert;
import org.junit.Test;

import io.rudin.minetest.tileserver.util.CoordinateResolver;
import io.rudin.minetest.tileserver.util.CoordinateResolver.MapBlockCoordinateInfo;
import io.rudin.minetest.tileserver.util.CoordinateResolver.TileInfo;

public class CoordinateResolverTest {

	@Test
	public void testSimpleTiletoBlock() {
		MapBlockCoordinateInfo blockInfo = CoordinateResolver.fromTile(1, 2, 9);
		Assert.assertEquals(16, blockInfo.x);
		Assert.assertEquals(-33, blockInfo.z);
		Assert.assertEquals(16, blockInfo.height, 0.1);
		Assert.assertEquals(16, blockInfo.width, 0.1);

		blockInfo = CoordinateResolver.fromTile(2, 2, 9);
		Assert.assertEquals(32, blockInfo.x);
		Assert.assertEquals(-33, blockInfo.z);
		Assert.assertEquals(16, blockInfo.height, 0.1);
		Assert.assertEquals(16, blockInfo.width, 0.1);
	}

	@Test
	public void testZoomedTile() {
		MapBlockCoordinateInfo blockInfo = CoordinateResolver.fromTile(0, 0, 13);
		Assert.assertEquals(0, blockInfo.x);
		Assert.assertEquals(-1, blockInfo.z);
		Assert.assertEquals(1, blockInfo.height, 0.1);
		Assert.assertEquals(1, blockInfo.width, 0.1);

		blockInfo = CoordinateResolver.fromTile(1, 2, 13);
		Assert.assertEquals(1, blockInfo.x);
		Assert.assertEquals(-3, blockInfo.z);
		Assert.assertEquals(1, blockInfo.height, 0.1);
		Assert.assertEquals(1, blockInfo.width, 0.1);
	}

	@Test
	public void testSimpleBlockToTile() {
		
		TileInfo tileInfo = CoordinateResolver.fromCoordinates(16, -32);
		Assert.assertEquals(13, tileInfo.zoom);
		Assert.assertEquals(1, tileInfo.width, 0.1);
		Assert.assertEquals(1, tileInfo.height, 0.1);
		Assert.assertEquals(16, tileInfo.x);
		Assert.assertEquals(33, tileInfo.y);
		
		tileInfo = CoordinateResolver.fromCoordinates(32, -32);
		Assert.assertEquals(13, tileInfo.zoom);
		Assert.assertEquals(1, tileInfo.width, 0.1);
		Assert.assertEquals(1, tileInfo.height, 0.1);
		Assert.assertEquals(32, tileInfo.x);
		Assert.assertEquals(33, tileInfo.y);
		
		tileInfo = CoordinateResolver.fromCoordinates(33, -32);
		Assert.assertEquals(13, tileInfo.zoom);
		Assert.assertEquals(1, tileInfo.width, 0.1);
		Assert.assertEquals(1, tileInfo.height, 0.1);
		Assert.assertEquals(33, tileInfo.x);
		Assert.assertEquals(33, tileInfo.y);
	}
	
	@Test
	public void testTileZoomOut() {
		TileInfo tileInfo = CoordinateResolver.fromCoordinates(32, -32);
		Assert.assertEquals(13, tileInfo.zoom);
		Assert.assertEquals(1, tileInfo.width, 0.1);
		Assert.assertEquals(1, tileInfo.height, 0.1);
		Assert.assertEquals(32, tileInfo.x);
		Assert.assertEquals(33, tileInfo.y);
		
		tileInfo = tileInfo.toZoom(12); //zoom out
		Assert.assertEquals(12, tileInfo.zoom);
		Assert.assertEquals(2, tileInfo.width, 0.1);
		Assert.assertEquals(2, tileInfo.height, 0.1);
		Assert.assertEquals(16, tileInfo.x);
		Assert.assertEquals(16, tileInfo.y);

		tileInfo = tileInfo.toZoom(11); //zoom out
		Assert.assertEquals(11, tileInfo.zoom);
		Assert.assertEquals(4, tileInfo.width, 0.1);
		Assert.assertEquals(4, tileInfo.height, 0.1);
		Assert.assertEquals(8, tileInfo.x);
		Assert.assertEquals(8, tileInfo.y);

		tileInfo = tileInfo.toZoom(10); //zoom out
		Assert.assertEquals(10, tileInfo.zoom);
		Assert.assertEquals(8, tileInfo.width, 0.1);
		Assert.assertEquals(8, tileInfo.height, 0.1);
		Assert.assertEquals(4, tileInfo.x);
		Assert.assertEquals(4, tileInfo.y);

		tileInfo = tileInfo.toZoom(9); //zoom out
		Assert.assertEquals(9, tileInfo.zoom);
		Assert.assertEquals(16, tileInfo.width, 0.1);
		Assert.assertEquals(16, tileInfo.height, 0.1);
		Assert.assertEquals(2, tileInfo.x);
		Assert.assertEquals(2, tileInfo.y);

	}

	@Test
	public void testTileZoomIn() {
		TileInfo tileInfo = CoordinateResolver.fromCoordinates(32, -32).toZoom(9);
		Assert.assertEquals(9, tileInfo.zoom);
		Assert.assertEquals(16, tileInfo.width, 0.1);
		Assert.assertEquals(16, tileInfo.height, 0.1);
		Assert.assertEquals(2, tileInfo.x);
		Assert.assertEquals(2, tileInfo.y);
		
		tileInfo = tileInfo.toZoom(10); //zoom in
		Assert.assertEquals(10, tileInfo.zoom);
		Assert.assertEquals(8, tileInfo.width, 0.1);
		Assert.assertEquals(8, tileInfo.height, 0.1);
		Assert.assertEquals(4, tileInfo.x);
		Assert.assertEquals(4, tileInfo.y);
		
	}
	
	/*

Zoom: 9
 __ __ __ __
|0 |1 |2 |3 |
|_0|_0|_0|_0|
|0 |1 |2 |3 |
|_1|_1|_1|_1|


Zoom: 8
 _____ _____
|0    |1    |
|     |     |
|     |     |
|____0|____0|

	 */
	
	@Test
	public void testTileZoomIn2() {
		TileInfo tileInfo = CoordinateResolver.fromCoordinates(16, -16).toZoom(9);
		Assert.assertEquals(9, tileInfo.zoom);
		Assert.assertEquals(16, tileInfo.width, 0.1);
		Assert.assertEquals(16, tileInfo.height, 0.1);
		Assert.assertEquals(1, tileInfo.x);
		Assert.assertEquals(1, tileInfo.y);
		
		tileInfo = tileInfo.toZoom(10); //zoom in
		Assert.assertEquals(10, tileInfo.zoom);
		Assert.assertEquals(8, tileInfo.width, 0.1);
		Assert.assertEquals(8, tileInfo.height, 0.1);
		Assert.assertEquals(2, tileInfo.x);
		Assert.assertEquals(2, tileInfo.y);
		
		tileInfo = tileInfo.toZoom(10); //zoom in
		Assert.assertEquals(10, tileInfo.zoom);
		Assert.assertEquals(8, tileInfo.width, 0.1);
		Assert.assertEquals(8, tileInfo.height, 0.1);
		Assert.assertEquals(2, tileInfo.x);
		Assert.assertEquals(2, tileInfo.y);
		
	}

	@Test
	public void dumpTileInfo(){
		TileInfo tileInfo = CoordinateResolver.fromCoordinates(6, -10);//96/-160
		System.out.println("Tile x=" + tileInfo.x + " y=" + tileInfo.y + " zoom=" + tileInfo.zoom);
	}

	@Test
	public void testTileZoomIterate() {
		TileInfo tileInfo = CoordinateResolver.fromCoordinates(32, -32);
		
		for (int i=CoordinateResolver.MAX_ZOOM; i>=CoordinateResolver.MIN_ZOOM; i--) {
			TileInfo zoomedTile = tileInfo.toZoom(i);

			MapBlockCoordinateInfo coordinateInfo = CoordinateResolver.fromTile(zoomedTile.x, zoomedTile.y, zoomedTile.zoom);

			System.out.println("Zoom: " + i);
			System.out.println("+Tile: " + zoomedTile);
			System.out.println("+MapBlock: " + coordinateInfo);
		}
		
	}


}
