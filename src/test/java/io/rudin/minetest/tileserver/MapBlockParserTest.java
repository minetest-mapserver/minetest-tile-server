package io.rudin.minetest.tileserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import org.junit.Before;
import org.junit.Test;

public class MapBlockParserTest {

	private static final InputStream MAPBLOCK_INPUT = MapBlockParserTest.class.getResourceAsStream("/mapblock.raw");
	
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
		
		for (int i=0; i<16; i++) {
			System.out.println("x="+i+": " + block.getNode(i, 0, 0));
		}

		System.out.println(block.getMetadata());
	}
	
}
