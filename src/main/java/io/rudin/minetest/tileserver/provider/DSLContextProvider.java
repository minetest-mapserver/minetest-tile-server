package io.rudin.minetest.tileserver.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariDataSource;

@Singleton
public class DSLContextProvider implements Provider<DSLContext> {

	@Inject
	public DSLContextProvider(HikariDataSource ds) {
		this.ds = ds;
	}
	
	private final HikariDataSource ds;
	
	@Override
	public DSLContext get() {
		return DSL.using(ds, SQLDialect.POSTGRES);
	}

}
