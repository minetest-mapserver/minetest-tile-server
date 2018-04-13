package io.rudin.minetest.tileserver.module;

import com.google.inject.AbstractModule;

import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.service.impl.FileTileCache;

public class ServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TileCache.class).to(FileTileCache.class);
	}
}
