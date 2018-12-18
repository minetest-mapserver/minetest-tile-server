package io.rudin.minetest.tileserver.util.coordinate;

public class Range<T> {
    public Range(T pos1, T pos2){
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public final T pos1, pos2;
}
