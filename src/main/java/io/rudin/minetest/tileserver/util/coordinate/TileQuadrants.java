package io.rudin.minetest.tileserver.util.coordinate;

public class TileQuadrants {
    public TileQuadrants(TileCoordinate upperLeft, TileCoordinate upperRight, TileCoordinate lowerLeft, TileCoordinate lowerRight) {
        this.upperLeft = upperLeft;
        this.upperRight = upperRight;
        this.lowerLeft = lowerLeft;
        this.lowerRight = lowerRight;
    }

    public final TileCoordinate upperLeft, upperRight, lowerLeft, lowerRight;

}
