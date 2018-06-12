package io.rudin.minetest.tileserver.provider;

import com.zaxxer.hikari.HikariDataSource;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class TileDBDSLContextProvider implements Provider<DSLContext> {

	@Inject
	public TileDBDSLContextProvider(@TileDB HikariDataSource ds) {
		this.ds = ds;
	}
	
	private final HikariDataSource ds;
	
	@Override
	public DSLContext get() {
		return DSL.using(ds, SQLDialect.POSTGRES);
	}

}
