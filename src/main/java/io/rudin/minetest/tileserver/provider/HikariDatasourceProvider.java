package io.rudin.minetest.tileserver.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class HikariDatasourceProvider implements Provider<HikariDataSource> {

	@Inject
	public HikariDatasourceProvider(HikariConfig cfg) {
		this.cfg = cfg;
	}
	
	private final HikariConfig cfg;
	
	@Override
	@Singleton
	public HikariDataSource get() {
		return new HikariDataSource(cfg);
	}

}
