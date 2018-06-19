package io.rudin.minetest.tileserver.service;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;

@Singleton
public class NoOpCache implements TileCache {

    private int putCount = 0;

    public int getPutCount(){
        return putCount;
    }

    @Override
    public void put(int x, int y, int z, byte[] data) throws IOException {
        putCount++;
    }

    @Override
    public byte[] get(int x, int y, int z) throws IOException {
        return new byte[0];
    }

    @Override
    public boolean has(int x, int y, int z) {
        return false;
    }

    @Override
    public void remove(int x, int y, int z) {

    }
}
