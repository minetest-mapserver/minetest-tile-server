package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Trainline;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import org.jooq.DSLContext;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.rudin.minetest.tileserver.blockdb.tables.Trainline.TRAINLINE;

@Singleton
public class TrainlineRoute implements Route {

	@Inject
	public TrainlineRoute(@MapDB DSLContext ctx) {
		this.ctx = ctx;
	}
	
	private final DSLContext ctx;
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");

		return ctx
				.selectFrom(TRAINLINE)
				.fetch()
				.into(Trainline.class);
	}

}
