package io.rudin.minetest.tileserver;

import org.junit.Test;

public class MapBlockPositionTest {

	@Test
	public void test() {

		int x=1, y=1, z=0;
		int position = x + (y * 16) + (z * 256);

		System.out.println(position);
		
	}
	
}
