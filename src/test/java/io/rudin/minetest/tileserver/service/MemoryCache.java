package io.rudin.minetest.tileserver.service;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class MemoryCache implements TileCache {

    public int putCount = 0, hitCount = 0, missCount = 0, checkCount = 0, removeCount = 0;

    private final Map<String, byte[]> cache = new HashMap<>();

    private String getKey(int x, int y, int z){
        return x + "/" + y + "/" + z;
    }

    @Override
    public void put(int x, int y, int z, byte[] data) throws IOException {
        final String key = getKey(x,y,z);
        cache.put(key, data);
        putCount++;
    }

    @Override
    public byte[] get(int x, int y, int z) throws IOException {
        final String key = getKey(x,y,z);
        byte[] data = cache.get(key);

        if (data == null)
            missCount++;
        else
            hitCount++;

        return data;
    }

    @Override
    public boolean has(int x, int y, int z){
        checkCount++;
        final String key = getKey(x,y,z);
        return cache.containsKey(key);
    }

    @Override
    public void remove(int x, int y, int z) {
        final String key = getKey(x,y,z);
        cache.remove(key);
        removeCount++;
    }
}
