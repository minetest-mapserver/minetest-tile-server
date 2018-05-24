package io.rudin.minetest.tileserver.route;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jooq.DSLContext;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Timestamp;

@Singleton
public class PlayerRoute implements Route {

	@Inject
	public PlayerRoute(DSLContext ctx) {
		this.ctx = ctx;
	}
	
	private final DSLContext ctx;
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");

		Timestamp ts = new Timestamp(System.currentTimeMillis() - (3600L*1000L));

		return ctx
				.selectFrom(PLAYER)
				.where(PLAYER.MODIFICATION_DATE.ge(ts))
				.fetch()
				.into(Player.class);
	}

}
