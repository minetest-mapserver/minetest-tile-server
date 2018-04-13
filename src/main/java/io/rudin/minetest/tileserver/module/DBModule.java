package io.rudin.minetest.tileserver.module;

import org.jooq.DSLContext;

import com.google.inject.AbstractModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.rudin.minetest.tileserver.provider.DSLContextProvider;
import io.rudin.minetest.tileserver.provider.HikariConfigProvider;
import io.rudin.minetest.tileserver.provider.HikariDatasourceProvider;

public class DBModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(HikariConfig.class).toProvider(HikariConfigProvider.class);
		bind(HikariDataSource.class).toProvider(HikariDatasourceProvider.class);
		bind(DSLContext.class).toProvider(DSLContextProvider.class);
	}
	
}
