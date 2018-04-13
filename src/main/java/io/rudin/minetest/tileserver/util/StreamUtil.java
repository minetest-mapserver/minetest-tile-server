package io.rudin.minetest.tileserver.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {
	
	/**
	 * Copies a stream with progress callback
	 * @param input
	 * @param output
	 * @throws IOException
	 * @return bytes copied
	 */
	public static long copyStream(InputStream input, OutputStream output) throws IOException
	{
		byte[] buffer = new byte[1000*1024]; //1mb
		long bytes = 0;

		do
		{
			int count = input.read(buffer);
			if (count <= 0)
				break;

			output.write(buffer, 0, count);
			bytes += count;
		}
		while(true);

		return bytes;
	}

}
