package io.rudin.minetest.tileserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapBlock {

	public MapBlock(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final int x,y,z;

	public int version;
	
	public boolean underground;
	
	public byte[] mapData;

	public byte[] metadata;
	public int metadataLength;

	public final Map<Integer, String> mapping = new HashMap<>();

	public static int toPosition(int x, int y, int z){
		return x + (y * 16) + (z * 256);
	}

	public Optional<String> getNode(int x, int y, int z) {
		if (mapping.isEmpty())
			return Optional.empty();

		int position = toPosition(x,y,z);
		
		int id = MapBlockParser.readU16(mapData, position * 2);
		
		if (mapping.containsKey(id))
			return Optional.of(mapping.get(id));
		else
			return Optional.empty();
	}
	
	public boolean isEmpty() {
		return mapping.isEmpty();
	}
	
}
