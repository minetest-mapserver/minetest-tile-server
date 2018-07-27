package io.rudin.minetest.tileserver.parser;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    public int size;

    public final List<Item> items = new ArrayList<>();

    @Override
    public String toString() {
        return "Inventory{" +
                "size=" + size +
                ", items=" + items +
                '}';
    }
}
