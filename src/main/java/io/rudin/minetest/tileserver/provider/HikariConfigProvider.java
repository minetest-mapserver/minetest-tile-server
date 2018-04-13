package io.rudin.minetest.tileserver.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.zaxxer.hikari.HikariConfig;

import io.rudin.minetest.tileserver.config.TileServerConfig;

@Singleton
public class HikariConfigProvider implements Provider<HikariConfig> {

	@Inject
	public HikariConfigProvider(TileServerConfig cfg) {
		this.cfg = cfg;
	}
	
	private final TileServerConfig cfg;
	
	@Override
	@Singleton
	public HikariConfig get() {
		
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername(cfg.minetestDatabaseUsername());
		hikariConfig.setPassword(cfg.minetestDatabasePassword());
		hikariConfig.setJdbcUrl(cfg.minetestDatabaseUrl());
		hikariConfig.setDriverClassName(cfg.minetestDatabaseDriver());
		return hikariConfig;
	}

}
