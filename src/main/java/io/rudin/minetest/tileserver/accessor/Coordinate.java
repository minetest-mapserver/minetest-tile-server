package io.rudin.minetest.tileserver.accessor;

import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

import java.util.Objects;

public class Coordinate {

    public Coordinate(BlocksRecord record){
        this.x = record.getPosx();
        this.y = record.getPosy();
        this.z = record.getPosz();
    }

    public Coordinate(MapBlock block){
        this.x = block.x;
        this.y = block.y;
        this.z = block.z;
    }

    public Coordinate(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final int x, y, z;

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x &&
                y == that.y &&
                z == that.z;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y, z);
    }
}
