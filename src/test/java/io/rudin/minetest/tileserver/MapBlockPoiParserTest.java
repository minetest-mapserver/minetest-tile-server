package io.rudin.minetest.tileserver;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class MapBlockPoiParserTest {

	private static final InputStream MAPBLOCK_INPUT = MapBlockPoiParserTest.class.getResourceAsStream("/poiblock.raw");
	
	@Before
	public void setup() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = MAPBLOCK_INPUT.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();
		
		mapblock = buffer.toByteArray();
		
		System.out.println("Raw size: " + mapblock.length);
	}
	
	private byte[] mapblock;
	
	@Test
	public void test() throws IllegalArgumentException, DataFormatException {
		MapBlock block = MapBlockParser.parse(mapblock, 0,0,0);
		
		System.out.println("Version: " + block.version);

		//listener block @ x13, y2, z2
		for (int x=0; x<16; x++) {
			for (int z=0; z<16; z++) {
				System.out.println("x=" + x + " z=" + z + ": " + block.getNode(x, 2, z));
			}
		}


	}
	
}
