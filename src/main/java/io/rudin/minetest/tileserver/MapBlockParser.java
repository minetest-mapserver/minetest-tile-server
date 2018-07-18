package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class MapBlockParser {

	public static int readU16(byte[] data, int offset) {
		return ((data[offset] & 0xff) << 8) | (data[offset + 1] & 0xff);
	}

	public static long readU32(byte[] data, int offset) {
		long value = data[offset+3] & 0xFF;
		value |= (data[offset+2] << 8) & 0xFFFF;
		value |= (data[offset+1] << 16) & 0xFFFFFF;
		value |= (data[offset] << 24) & 0xFFFFFFFF;
		return value;
	}


	public static MapBlock parse(BlocksRecord record) throws IllegalArgumentException {
		return parse(
				record.getData(),
				record.getPosx(),
				record.getPosy(),
				record.getPosz()
		);
	}

		/**
         * Ref: https://github.com/minetest/minetest/blob/master/doc/world_format.txt
         * Impl: https://github.com/minetest/minetestmapper/blob/master/BlockDecoder.cpp
         *
         * @param data
         * @return
         * @throws IllegalArgumentException
         * @throws DataFormatException
         */
	public static MapBlock parse(byte[] data, int x, int y, int z) throws IllegalArgumentException {
		if (data == null || data.length == 0)
			throw new IllegalArgumentException("invalid data");

		MapBlock block = new MapBlock(x,y,z);

		block.version = data[0];


		if (block.version != 28) {
			throw new IllegalArgumentException("block version not supported: " + block.version);
		}

		byte flags = data[1];

		block.underground = (flags & 0x01) == 0x01;
		//TODO: more flags


		//data[2] / [3] = lighting

		//data[4] = content_width (2)

		//data[5] = params_width (2)
		int dataOffset = 6;

		Inflater inflater = new Inflater();
		inflater.setInput(data, dataOffset, data.length - dataOffset);
		
		byte[] mapData = new byte[16384];

		try {
			int mapDataLength = inflater.inflate(mapData);

			if (mapDataLength != 16384) {
				throw new IllegalArgumentException("map data size does not line up: " + mapDataLength);
			}

		} catch (DataFormatException e){
			throw new IllegalArgumentException(e);
		}
		
		block.mapData = mapData;
		
		dataOffset += inflater.getTotalIn();

		inflater = new Inflater();
		inflater.setInput(data, dataOffset, data.length - dataOffset);

		block.metadata = new byte[1024*1024]; //1M

		try {
			//Dummy inflation to get size
			block.metadataLength = inflater.inflate(block.metadata);
			dataOffset += inflater.getTotalIn();

		} catch (Exception e){
			throw new IllegalArgumentException(e);
		}
		

		// Skip unused static objects
		
		int staticObjectVersion = data[dataOffset++];
		
		int staticObjectCount = readU16(data, dataOffset);
		dataOffset += 2;
		for (int i = 0; i < staticObjectCount; ++i) {
			dataOffset += 13;
			int dataSize = readU16(data, dataOffset);
			dataOffset += dataSize + 2;
		}

		dataOffset += 4; // Skip timestamp


		//version > 20

		int m_blockAirId = -1;
		int m_blockIgnoreId = -1;
		Map<Integer, String> m_nameMap = block.mapping;

		dataOffset++; // mapping version
		int numMappings = readU16(data, dataOffset);
		dataOffset += 2;
		for (int i = 0; i < numMappings; ++i) {
			int nodeId = readU16(data, dataOffset);
			dataOffset += 2;
			int nameLen = readU16(data, dataOffset);
			dataOffset += 2;
			String name = new String(data, dataOffset, nameLen);

			if (name.equals("air"))
				m_blockAirId = nodeId;
			else if (name.equals("ignore"))
				m_blockIgnoreId = nodeId;
			else
				m_nameMap.put(nodeId, name);

			dataOffset += nameLen;
		}
		
		//m_nameMap.put(m_blockAirId, "air");
		//m_nameMap.put(m_blockIgnoreId, "ignore");


		// Node timers
		//version > 25

		dataOffset++;
		int numTimers = readU16(data, dataOffset);
		dataOffset += 2;
		dataOffset += numTimers * 10;

		return block;
	}

}
