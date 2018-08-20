package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.parser.MetadataParser;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.DataFormatException;

public class MetadataParserTest {

	private static final InputStream MAPBLOCK_INPUT = MetadataParserTest.class.getResourceAsStream("/metadata.raw");
	
	@Before
	public void setup() throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = MAPBLOCK_INPUT.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}

		buffer.flush();

		metadata = buffer.toByteArray();
		
		System.out.println("Raw size: " + metadata.length);
	}
	
	private byte[] metadata;
	
	@Test
	public void test() throws IllegalArgumentException, DataFormatException {
		Metadata md = MetadataParser.parse(metadata, metadata.length);

		for (Map.Entry<Integer, Map<String, String>> entry : md.map.entrySet()) {
			System.out.println("Position: " + entry.getKey());

			for (Map.Entry<String, String> mdEntry: entry.getValue().entrySet()){
				System.out.println("- " + mdEntry.getKey() + "=" + mdEntry.getValue());
			}
		}

		System.out.println(md);


	}
	
}
