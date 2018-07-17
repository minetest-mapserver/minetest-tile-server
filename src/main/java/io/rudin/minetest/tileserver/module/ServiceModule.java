package io.rudin.minetest.tileserver.module;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.google.inject.AbstractModule;

import io.rudin.minetest.tileserver.ColorTable;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.provider.ColorTableProvider;
import io.rudin.minetest.tileserver.provider.ExecutorProvider;
import io.rudin.minetest.tileserver.service.EventBus;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.service.impl.DatabaseTileCache;
import io.rudin.minetest.tileserver.service.impl.EHTileCache;
import io.rudin.minetest.tileserver.service.impl.EventBusImpl;
import io.rudin.minetest.tileserver.service.impl.FileTileCache;
import org.jooq.util.jaxb.Database;

public class ServiceModule extends AbstractModule {

	public ServiceModule(TileServerConfig cfg) {
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	@Override
	protected void configure() {


		if (cfg.tileCacheType() == TileServerConfig.CacheType.DATABASE)
			bind(TileCache.class).to(DatabaseTileCache.class);

		else if (cfg.tileCacheType() == TileServerConfig.CacheType.EHCACHE)
			bind(TileCache.class).to(EHTileCache.class);

		else
			bind(TileCache.class).to(FileTileCache.class);

		bind(EventBus.class).to(EventBusImpl.class);
		bind(ColorTable.class).toProvider(ColorTableProvider.class);
		bind(ExecutorService.class).toProvider(ExecutorProvider.class);
		bind(ScheduledExecutorService.class).toProvider(ExecutorProvider.class);
	}
}
