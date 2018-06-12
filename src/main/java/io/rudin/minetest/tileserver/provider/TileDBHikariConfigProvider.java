package io.rudin.minetest.tileserver.provider;

import com.zaxxer.hikari.HikariConfig;
import io.rudin.minetest.tileserver.config.TileServerConfig;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class TileDBHikariConfigProvider implements Provider<HikariConfig> {

	@Inject
	public TileDBHikariConfigProvider(TileServerConfig cfg) {
		this.cfg = cfg;
	}
	
	private final TileServerConfig cfg;
	
	@Override
	@Singleton
	public HikariConfig get() {
		
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername(cfg.tileDatabaseUsername());
		hikariConfig.setPassword(cfg.tileDatabasePassword());
		hikariConfig.setJdbcUrl(cfg.tileDatabaseUrl());
		hikariConfig.setDriverClassName(cfg.tileDatabaseDriver());
		return hikariConfig;
	}

}
