package io.rudin.minetest.tileserver.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.rudin.minetest.tileserver.config.TileServerConfig;

@Singleton
public class MapDBDatasourceProvider implements Provider<DataSource> {

	@Inject
	public MapDBDatasourceProvider(TileServerConfig cfg) {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername(cfg.minetestDatabaseUsername());
		hikariConfig.setPassword(cfg.minetestDatabasePassword());
		hikariConfig.setJdbcUrl(cfg.minetestDatabaseUrl());
		hikariConfig.setDriverClassName(cfg.minetestDatabaseDriver());

		ds = new HikariDataSource(hikariConfig);
	}
	
	private final HikariDataSource ds;
	
	@Override
	@Singleton
	public DataSource get() {
		return ds;
	}

}
