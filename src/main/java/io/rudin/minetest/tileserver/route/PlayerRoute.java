package io.rudin.minetest.tileserver.route;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;
import static io.rudin.minetest.tileserver.blockdb.tables.PlayerMetadata.PLAYER_METADATA;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.PlayerMetadata;
import io.rudin.minetest.tileserver.entity.PlayerInfo;
import org.jooq.DSLContext;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

		List<Player> players = ctx
				.selectFrom(PLAYER)
				.where(PLAYER.MODIFICATION_DATE.ge(ts))
				.fetch()
				.into(Player.class);

		List<PlayerInfo> list = new ArrayList<>();

		for (Player player: players){

			List<PlayerMetadata> metadata = ctx
					.selectFrom(PLAYER_METADATA)
					.where(PLAYER_METADATA.PLAYER.eq(player.getName()))
					.fetchInto(PlayerMetadata.class);

			list.add(new PlayerInfo(player, metadata));
		}

		System.out.println(list.size());

		return list;
	}

}
