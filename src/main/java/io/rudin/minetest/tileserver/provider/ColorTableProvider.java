package io.rudin.minetest.tileserver.provider;

import javax.inject.Provider;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.ColorTable;

@Singleton
public class ColorTableProvider implements Provider<ColorTable> {

	@Override
	@Singleton
	public ColorTable get() {
		ColorTable colorTable = new ColorTable();
		colorTable.load(ColorTableProvider.class.getResourceAsStream("/colors.txt"));
		return colorTable;
	}

}
