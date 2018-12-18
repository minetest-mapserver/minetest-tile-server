package io.rudin.minetest.tileserver.util.coordinate;

public class MapBlockCoordinate {
    public MapBlockCoordinate(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MapBlockCoordinate(int x, int z){
        this.x = x;
        this.z = z;
        this.y = null;
    }

    public final int x, z;

    public final Integer y;

    public boolean hasYAxis(){
        return y != null;
    }
}
