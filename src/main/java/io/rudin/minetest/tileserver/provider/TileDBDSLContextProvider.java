package io.rudin.minetest.tileserver.provider;

import com.zaxxer.hikari.HikariDataSource;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Singleton
public class TileDBDSLContextProvider implements Provider<DSLContext> {

	@Inject
	public TileDBDSLContextProvider(@TileDB DataSource ds) {
		this.ds = ds;
	}
	
	private final DataSource ds;
	
	@Override
	public DSLContext get() {
		return DSL.using(ds, SQLDialect.POSTGRES);
	}

}
