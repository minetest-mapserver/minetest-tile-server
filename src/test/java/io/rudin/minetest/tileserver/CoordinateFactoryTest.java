package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.util.coordinate.*;
import org.junit.Assert;
import org.junit.Test;

public class CoordinateFactoryTest {

    @Test
    public void testCoordinateFromMapblock(){

        MapBlockCoordinate mapBlock = new MapBlockCoordinate(0,0);
        TileCoordinate tileCoordinate = CoordinateFactory.getTileCoordinateFromMapBlock(mapBlock);
        Assert.assertEquals(0, tileCoordinate.x);
        Assert.assertEquals(1, tileCoordinate.y);
        Assert.assertEquals(13, tileCoordinate.zoom);

        mapBlock = new MapBlockCoordinate(1,1);
        tileCoordinate = CoordinateFactory.getTileCoordinateFromMapBlock(mapBlock);
        Assert.assertEquals(1, tileCoordinate.x);
        Assert.assertEquals(0, tileCoordinate.y);
        Assert.assertEquals(13, tileCoordinate.zoom);

        mapBlock = new MapBlockCoordinate(-1,-1);
        tileCoordinate = CoordinateFactory.getTileCoordinateFromMapBlock(mapBlock);
        Assert.assertEquals(-1, tileCoordinate.x);
        Assert.assertEquals(2, tileCoordinate.y);
        Assert.assertEquals(13, tileCoordinate.zoom);

    }

    @Test
    public void testMapBlocksInTile(){

        Range<MapBlockCoordinate> range = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(0, 0, 13));
        Assert.assertEquals(0, range.pos1.x);
        Assert.assertEquals(-1, range.pos1.z);
        Assert.assertEquals(0, range.pos2.x);
        Assert.assertEquals(-1, range.pos2.z);

        range = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(-1, -1, 13));
        Assert.assertEquals(-1, range.pos1.x);
        Assert.assertEquals(0, range.pos1.z);
        Assert.assertEquals(-1, range.pos2.x);
        Assert.assertEquals(0, range.pos2.z);

        range = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(0, 0, 12));
        Assert.assertEquals(0, range.pos1.x);
        Assert.assertEquals(-1, range.pos1.z);
        Assert.assertEquals(1, range.pos2.x);
        Assert.assertEquals(-2, range.pos2.z);

        range = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(1, 1, 12));
        Assert.assertEquals(2, range.pos1.x);
        Assert.assertEquals(-3, range.pos1.z);
        Assert.assertEquals(3, range.pos2.x);
        Assert.assertEquals(-4, range.pos2.z);

        range = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(2, 2, 12));
        Assert.assertEquals(4, range.pos1.x);
        Assert.assertEquals(-5, range.pos1.z);
        Assert.assertEquals(5, range.pos2.x);
        Assert.assertEquals(-6, range.pos2.z);

        range = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(0, 0, 11));
        Assert.assertEquals(0, range.pos1.x);
        Assert.assertEquals(-1, range.pos1.z);
        Assert.assertEquals(3, range.pos2.x);
        Assert.assertEquals(-4, range.pos2.z);

        range = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(1, 1, 11));
        Assert.assertEquals(4, range.pos1.x);
        Assert.assertEquals(-5, range.pos1.z);
        Assert.assertEquals(7, range.pos2.x);
        Assert.assertEquals(-8, range.pos2.z);

    }

    @Test
    public void testZoomedQuadrantsFromTile(){

        TileQuadrants quadrants = CoordinateFactory.getZoomedQuadrantsFromTile(new TileCoordinate(0, 0, 12));
        Assert.assertEquals(0, quadrants.upperLeft.x);
        Assert.assertEquals(0, quadrants.upperLeft.y);
        Assert.assertEquals(13, quadrants.upperLeft.zoom);
        Assert.assertEquals(1, quadrants.upperRight.x);
        Assert.assertEquals(0, quadrants.upperRight.y);
        Assert.assertEquals(13, quadrants.upperRight.zoom);
        Assert.assertEquals(0, quadrants.lowerLeft.x);
        Assert.assertEquals(1, quadrants.lowerLeft.y);
        Assert.assertEquals(13, quadrants.lowerLeft.zoom);
        Assert.assertEquals(1, quadrants.lowerRight.x);
        Assert.assertEquals(1, quadrants.lowerRight.y);
        Assert.assertEquals(13, quadrants.lowerRight.zoom);

        quadrants = CoordinateFactory.getZoomedQuadrantsFromTile(new TileCoordinate(-2, -2, 12));
        Assert.assertEquals(-4, quadrants.upperLeft.x);
        Assert.assertEquals(-4, quadrants.upperLeft.y);
        Assert.assertEquals(13, quadrants.upperLeft.zoom);
        Assert.assertEquals(-3, quadrants.upperRight.x);
        Assert.assertEquals(-4, quadrants.upperRight.y);
        Assert.assertEquals(13, quadrants.upperRight.zoom);
        Assert.assertEquals(-4, quadrants.lowerLeft.x);
        Assert.assertEquals(-3, quadrants.lowerLeft.y);
        Assert.assertEquals(13, quadrants.lowerLeft.zoom);
        Assert.assertEquals(-3, quadrants.lowerRight.x);
        Assert.assertEquals(-3, quadrants.lowerRight.y);
        Assert.assertEquals(13, quadrants.lowerRight.zoom);

    }

}
