package io.rudin.minetest.tileserver.module;

import com.google.inject.Key;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import io.rudin.minetest.tileserver.provider.*;
import org.jooq.DSLContext;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DBModule extends AbstractModule {

	public DBModule(TileServerConfig cfg) {
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;


	@Override
	protected void configure() {
		bind(Key.get(DataSource.class, MapDB.class)).toProvider(MapDBDatasourceProvider.class);
		bind(Key.get(DSLContext.class, MapDB.class)).toProvider(MapDBDSLContextProvider.class);

		if (cfg.tileCacheType() == TileServerConfig.CacheType.DATABASE) {
			bind(Key.get(DataSource.class, TileDB.class)).toProvider(TileDBDatasourceProvider.class);
			bind(Key.get(DSLContext.class, TileDB.class)).toProvider(TileDBDSLContextProvider.class);
		}
	}
	
}
