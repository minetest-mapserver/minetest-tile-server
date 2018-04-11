package io.rudin.minetest.tileserver.cache;

import java.io.IOException;

public interface TileCache {

	void put(int x, int y, int z, byte[] data) throws IOException;
	
	byte[] get(int x, int y, int z) throws IOException;

	boolean has(int x, int y, int z);
	
}
