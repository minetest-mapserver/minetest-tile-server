package io.rudin.minetest.tileserver.service;

import java.io.IOException;

public interface TileCache {

	void put(int x, int y, int z, byte[] data) throws IOException;
	
	byte[] get(int x, int y, int z) throws IOException;

	boolean has(int x, int y, int z);
	
	void remove(int x, int y, int z);

	long getLatestTimestamp();

}
