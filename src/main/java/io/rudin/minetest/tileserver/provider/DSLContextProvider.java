package io.rudin.minetest.tileserver.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.module.LoggingExecuteListener;
import org.jooq.DSLContext;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class DSLContextProvider implements Provider<DSLContext> {

	@Inject
	public DSLContextProvider(HikariDataSource ds, TileServerConfig cfg) {
		this.ds = ds;
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	private final HikariDataSource ds;
	
	@Override
	public DSLContext get() {
		DSLContext ctx = DSL.using(ds, SQLDialect.POSTGRES);

		if (cfg.logQueryPerformance())
			ctx.configuration().set(new LoggingExecuteListener());

		return ctx;
	}

}
