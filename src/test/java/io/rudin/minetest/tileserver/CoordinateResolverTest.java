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
		Assert.assertEquals(-32, blockInfo.z);
		Assert.assertEquals(16, blockInfo.height, 0.1);
		Assert.assertEquals(16, blockInfo.width, 0.1);
		
		blockInfo = CoordinateResolver.fromTile(2, 2, 9);
		Assert.assertEquals(32, blockInfo.x);
		Assert.assertEquals(-32, blockInfo.z);
		Assert.assertEquals(16, blockInfo.height, 0.1);
		Assert.assertEquals(16, blockInfo.width, 0.1);
	}

	@Test
	public void testSimpleBlockToTile() {
		
		TileInfo tileInfo = CoordinateResolver.fromCoordinates(16, -32);
		Assert.assertEquals(1, tileInfo.x);
		Assert.assertEquals(2, tileInfo.y);
		
	}

}
