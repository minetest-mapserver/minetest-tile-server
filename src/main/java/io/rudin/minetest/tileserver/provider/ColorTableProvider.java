package io.rudin.minetest.tileserver.provider;

import javax.inject.Provider;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.ColorTable;
import io.rudin.minetest.tileserver.FetchMap;

@Singleton
public class ColorTableProvider implements Provider<ColorTable> {

	@Override
	@Singleton
	public ColorTable get() {
		ColorTable colorTable = new ColorTable();
		colorTable.load(FetchMap.class.getResourceAsStream("/colors.txt"));
		return colorTable;
	}

}
