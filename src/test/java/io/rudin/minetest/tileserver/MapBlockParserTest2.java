package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.parser.MetadataParser;
import io.rudin.minetest.tileserver.util.MapBlock;
import io.rudin.minetest.tileserver.util.MapBlockParser;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class MapBlockParserTest2 {

	private static final InputStream MAPBLOCK_INPUT = MapBlockParserTest2.class.getResourceAsStream("/mapblock_-111.1.26.raw");
	
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
	public void test() throws Exception {
		MapBlock block = MapBlockParser.parse(mapblock, 0,0,0);
		
		System.out.println("Version: " + block.version);
		
		for (int i=0; i<16; i++) {
			System.out.println("x="+i+": " + block.getNode(i, 0, 0));
		}

		System.out.println(block.mapping);
		System.out.println("Metadata length: " + block.metadataLength);

		try (OutputStream output = new FileOutputStream("target/metadata.raw")){
			output.write(block.metadata, 0, block.metadataLength);
		}

		MetadataParser.parse(block.metadata, block.metadataLength);
	}
	
}
