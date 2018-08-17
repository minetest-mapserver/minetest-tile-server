package io.rudin.minetest.tileserver.service;

import java.io.IOException;

public interface TileCache {

	void put(int layerId, int x, int y, int z, byte[] data) throws IOException;
	
	byte[] get(int layerId, int x, int y, int z) throws IOException;

	boolean has(int layerId, int x, int y, int z);
	
	void remove(int layerId, int x, int y, int z);

	long getLatestTimestamp();

	void close();

}
