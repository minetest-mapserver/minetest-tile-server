package io.rudin.minetest.tileserver.module;

import com.google.inject.AbstractModule;

import io.rudin.minetest.tileserver.ColorTable;
import io.rudin.minetest.tileserver.provider.ColorTableProvider;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.service.impl.FileTileCache;

public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TileCache.class).to(FileTileCache.class);
		bind(ColorTable.class).toProvider(ColorTableProvider.class);
	}
}
