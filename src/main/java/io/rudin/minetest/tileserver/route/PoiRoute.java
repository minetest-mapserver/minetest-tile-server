package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Poi;
import org.jooq.DSLContext;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

import static io.rudin.minetest.tileserver.blockdb.tables.PlayerMetadata.PLAYER_METADATA;
import static io.rudin.minetest.tileserver.blockdb.tables.Poi.POI;

@Singleton
public class PoiRoute implements Route {

	@Inject
	public PoiRoute(DSLContext ctx) {
		this.ctx = ctx;
	}
	
	private final DSLContext ctx;
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");

		return ctx
				.selectFrom(POI)
				.fetch()
				.into(Poi.class);
	}

}
