package io.rudin.minetest.tileserver.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Per block metadata
 */
public class Metadata {
    @Override
    public String toString() {
        return "Metadata{" +
                "inventories=" + inventories +
                ", map=" + map +
                '}';
    }

    public final Map<Integer, Map<String, Inventory>> inventories = new HashMap<>();

    public final Map<Integer, Map<String, String>> map = new HashMap<>();

}
