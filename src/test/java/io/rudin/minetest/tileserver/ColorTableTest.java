package io.rudin.minetest.tileserver;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import io.rudin.minetest.tileserver.ColorTable.Color;

public class ColorTableTest {

	@Test
	public void test() {
		
		ColorTable table = new ColorTable();
		table.load(ColorTableTest.class.getResourceAsStream("/colors.txt"));
		
		Map<String, Color> map = table.getColorMap();
		
		Assert.assertTrue(map.size() > 20);
		
		Color color = map.get("vessels:shelf");
		
		Assert.assertEquals(128, color.r);
		Assert.assertEquals(99, color.g);
		Assert.assertEquals(55, color.b);
		
	}
	
}
