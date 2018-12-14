package io.rudin.minetest.tileserver.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class MapDBDSLContextProvider implements Provider<DSLContext> {

	@Inject
	public MapDBDSLContextProvider(@MapDB DataSource ds, TileServerConfig cfg) {
		this.ds = ds;
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	private final DataSource ds;
	
	@Override
	public DSLContext get() {
		return DSL.using(ds, cfg.minetestDatabaseDialect());
	}

}
