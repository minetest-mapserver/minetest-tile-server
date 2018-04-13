package io.rudin.minetest.tileserver.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class WhiteTile {

	private static final byte[] png;
	
	static {
		InputStream inputStream = WhiteTile.class.getResourceAsStream("/osm-white.png");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try {
			StreamUtil.copyStream(inputStream, output);
			png = output.toByteArray();
		} catch (Exception e){
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static byte[] getPNG(){
		return png;
	}
}
	