package io.rudin.minetest.tileserver.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.TileDB;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Singleton
public class TileDBDatasourceProvider implements Provider<DataSource> {

	@Inject
	public TileDBDatasourceProvider(TileServerConfig cfg) {
		this.cfg = cfg;
	}
	
	private final TileServerConfig cfg;
	
	@Override
	@Singleton
	public DataSource get() {

		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername(cfg.tileDatabaseUsername());
		hikariConfig.setPassword(cfg.tileDatabasePassword());
		hikariConfig.setJdbcUrl(cfg.tileDatabaseUrl());
		hikariConfig.setDriverClassName(cfg.tileDatabaseDriver());

		return new HikariDataSource(hikariConfig);
	}

}
