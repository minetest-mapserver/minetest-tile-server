package io.rudin.minetest.tileserver.util.coordinate;

import java.awt.image.BufferedImage;

public class CoordinateFactory {

    //Max zoom, 1:1 mapblock:tile
    public static int MAX_ZOOM = 13;

    /**
     * Returns a tile coordinate translated from a mapblock
     * @param mapBlock
     * @return
     */
    public static TileCoordinate getTileCoordinateFromMapBlock(MapBlockCoordinate mapBlock){
        //Inverted z-axis
        return new TileCoordinate(mapBlock.x, mapBlock.z * -1, MAX_ZOOM);
    }

    /**
     * Returns a range of blocks from the tilecoordinate
     * @param tile
     * @return
     */
    public static Range<MapBlockCoordinate> getMapBlocksInTile(TileCoordinate tile){
        int scaleDiff = MAX_ZOOM - tile.zoom; //0(highest zoom) .. 12(lowest zoom)
        int scale = (int) Math.pow(2, scaleDiff); //1,4,8,16,32...4096

        int mapBlockX1 = tile.x * scale;
        int mapBlockZ1 = tile.y * scale * -1;

        int mapBlockX2 = mapBlockX1 + scale-1;
        int mapBlockZ2 = mapBlockZ1 + ((scale-1) * -1);

        return new Range<>(
                new MapBlockCoordinate(mapBlockX1, mapBlockZ1),
                new MapBlockCoordinate(mapBlockX2, mapBlockZ2)
        );
    }

    public static TileQuadrants getZoomedQuadrantsFromTile(TileCoordinate tile){
        if (tile.zoom >= MAX_ZOOM || tile.zoom < 1)
            throw new IllegalArgumentException("invalid zoom: " + tile.zoom);

        int nextZoom = tile.zoom + 1;

        int nextZoomX = tile.x * 2;
        int nextZoomY = tile.y * 2;

        TileCoordinate upperLeft = new TileCoordinate(nextZoomX, nextZoomY, nextZoom);
        TileCoordinate upperRight = new TileCoordinate(nextZoomX + 1, nextZoomY, nextZoom);
        TileCoordinate lowerLeft = new TileCoordinate(nextZoomX, nextZoomY + 1, nextZoom);
        TileCoordinate lowerRight = new TileCoordinate(nextZoomX + 1, nextZoomY + 1, nextZoom);


        return new TileQuadrants(upperLeft, upperRight, lowerLeft, lowerRight);
    }



}
