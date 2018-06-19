package io.rudin.minetest.tileserver;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;


public class ColorTableTest {

	@Test
	public void test() {
		
		ColorTable table = new ColorTable();
		table.load(ColorTableTest.class.getResourceAsStream("/colors.txt"));
		table.load(ColorTableTest.class.getResourceAsStream("/vanessa.txt"));

		Map<String, ColorTable.RGBData> map = table.getColorMap();
		
		Assert.assertTrue(map.size() > 10000);

		ColorTable.RGBData color = map.get("vessels:shelf");
		
		Assert.assertEquals(128, color.r);
		Assert.assertEquals(99, color.g);
		Assert.assertEquals(55, color.b);

		color.addComponent(-2);

		Assert.assertEquals(128-2, color.r);
		Assert.assertEquals(99-2, color.g);
		Assert.assertEquals(55-2, color.b);

		color.addComponent(6);

		Assert.assertEquals(128+4, color.r);
		Assert.assertEquals(99+4, color.g);
		Assert.assertEquals(55+4, color.b);

	}
	
}
