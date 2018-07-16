package io.rudin.minetest.tileserver.route;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;
import static io.rudin.minetest.tileserver.blockdb.tables.PlayerMetadata.PLAYER_METADATA;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.accessor.BlocksRecordAccessor;
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
	public PlayerRoute(DSLContext ctx, TileServerConfig cfg) {
		this.ctx = ctx;
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	private final DSLContext ctx;
	
	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");

		long start = System.currentTimeMillis();

		Timestamp ts = new Timestamp(System.currentTimeMillis() - (3600L*1000L));

		List<Player> players = ctx
				.selectFrom(PLAYER)
				.where(PLAYER.MODIFICATION_DATE.ge(ts))
				.fetch()
				.into(Player.class);

		List<PlayerInfo> list = new ArrayList<>();

		for (Player player: players) {

			List<PlayerMetadata> metadata = ctx
					.selectFrom(PLAYER_METADATA)
					.where(PLAYER_METADATA.PLAYER.eq(player.getName()))
					.fetchInto(PlayerMetadata.class);

			list.add(new PlayerInfo(player, metadata));
		}

		long diff = System.currentTimeMillis() - start;

		if (diff > 250 && cfg.logQueryPerformance()){
			logger.warn("getTopyDownYStride took {} ms", diff);
		}

		return list;
	}

}
