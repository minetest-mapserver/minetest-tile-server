package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Shop;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import org.jooq.DSLContext;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.rudin.minetest.tileserver.blockdb.tables.Shop.SHOP;

@Singleton
public class ShopRoute implements Route {

	@Inject
	public ShopRoute(@MapDB DSLContext ctx) {
		this.ctx = ctx;
	}
	
	private final DSLContext ctx;
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");

		return ctx
				.selectFrom(SHOP)
				.fetch()
				.into(Shop.class);
	}

}
