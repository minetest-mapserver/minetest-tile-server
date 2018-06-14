package io.rudin.minetest.tileserver.module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.AbstractModule;

import io.rudin.minetest.tileserver.ColorTable;
import io.rudin.minetest.tileserver.provider.ColorTableProvider;
import io.rudin.minetest.tileserver.provider.ExecutorProvider;
import io.rudin.minetest.tileserver.service.EventBus;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.service.impl.DatabaseTileCache;
import io.rudin.minetest.tileserver.service.impl.EventBusImpl;

public class ServiceModule extends AbstractModule {

	public ServiceModule(){
		this(DatabaseTileCache.class);
	}

	public ServiceModule(Class<? extends TileCache> cacheImpl){
		this.cacheImpl = cacheImpl;
	}

	private final Class<? extends TileCache> cacheImpl;

	@Override
	protected void configure() {
		bind(TileCache.class).to(cacheImpl);
		//bind(TileCache.class).to(FileTileCache.class);
		bind(EventBus.class).to(EventBusImpl.class);
		bind(ColorTable.class).toProvider(ColorTableProvider.class);
		bind(ExecutorService.class).toProvider(ExecutorProvider.class);
		bind(ScheduledExecutorService.class).toProvider(ExecutorProvider.class);
	}
}
