package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import io.rudin.minetest.tileserver.blockdb.tables.pojos.PlayerMetadata;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.entity.PlayerInfo;
import org.jooq.DSLContext;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.rudin.minetest.tileserver.blockdb.tables.Player.PLAYER;
import static io.rudin.minetest.tileserver.blockdb.tables.PlayerMetadata.PLAYER_METADATA;

@Singleton
public class ConfigRoute implements Route {

	@Inject
	public ConfigRoute(TileServerConfig cfg){
		config.put("matomo.id", cfg.matomoTrackerId());
		config.put("matomo.url", cfg.matomoTrackerUrl());
		config.put("parser.poi", cfg.parserPoiEnable());
		config.put("parser.travelnet", cfg.parserTravelnetEnable());
	}

	private final Map<String, Object> config = new HashMap<>();


	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");
		return config;
	}

}
