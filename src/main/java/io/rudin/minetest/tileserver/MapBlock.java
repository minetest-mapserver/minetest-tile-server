package io.rudin.minetest.tileserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapBlock {

	public int version;
	
	public boolean underground;
	
	public byte[] mapData;
	public final Map<Integer, String> mapping = new HashMap<>();
	
	public Optional<String> getNode(int x, int y, int z) {
		if (mapping.isEmpty())
			return Optional.empty();

		int position = x + (y * 16) + (z * 256);
		
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
