package io.rudin.minetest.tileserver.route;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;
import static io.rudin.minetest.tileserver.blockdb.tables.PlayerMetadata.PLAYER_METADATA;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.accessor.BlocksRecordAccessor;
import io.rudin.minetest.tileserver.accessor.PlayerInfoAccessor;
import io.rudin.minetest.tileserver.blockdb.tables.pojos.PlayerMetadata;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.entity.PlayerInfo;
import org.jooq.DSLContext;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class PlayerRoute implements Route {

	private static final Logger logger = LoggerFactory.getLogger(PlayerRoute.class);

	@Inject
	public PlayerRoute(PlayerInfoAccessor playerInfoAccessor, TileServerConfig cfg) {
		this.playerInfoAccessor = playerInfoAccessor;
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	private final PlayerInfoAccessor playerInfoAccessor;
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");

		long start = System.currentTimeMillis();

		Timestamp ts = new Timestamp(System.currentTimeMillis() - (3600L*1000L));

		return playerInfoAccessor.getPlayersSince(ts);
	}

}
