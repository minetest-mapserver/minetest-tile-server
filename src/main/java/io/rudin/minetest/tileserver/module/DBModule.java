package io.rudin.minetest.tileserver.module;

import com.google.inject.Key;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import io.rudin.minetest.tileserver.provider.*;
import org.jooq.DSLContext;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HikariConfig.class).toProvider(HikariConfigProvider.class);
		bind(HikariDataSource.class).toProvider(HikariDatasourceProvider.class);
		bind(DSLContext.class).toProvider(DSLContextProvider.class);

		bind(Key.get(HikariConfig.class, TileDB.class)).toProvider(TileDBHikariConfigProvider.class);
		bind(Key.get(HikariDataSource.class, TileDB.class)).toProvider(TileDBHikariDatasourceProvider.class);
		bind(Key.get(DSLContext.class, TileDB.class)).toProvider(TileDBDSLContextProvider.class);
	}
	
}
