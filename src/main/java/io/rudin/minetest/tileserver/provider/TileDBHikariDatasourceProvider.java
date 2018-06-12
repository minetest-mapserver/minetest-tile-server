package io.rudin.minetest.tileserver.provider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.rudin.minetest.tileserver.qualifier.TileDB;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class TileDBHikariDatasourceProvider implements Provider<HikariDataSource> {

	@Inject
	public TileDBHikariDatasourceProvider(@TileDB HikariConfig cfg) {
		this.cfg = cfg;
	}
	
	private final HikariConfig cfg;
	
	@Override
	@Singleton
	public HikariDataSource get() {
		return new HikariDataSource(cfg);
	}

}
