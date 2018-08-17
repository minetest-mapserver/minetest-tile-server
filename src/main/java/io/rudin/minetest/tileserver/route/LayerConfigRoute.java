package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.config.LayerConfig;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class LayerConfigRoute implements Route {

	@Inject
	public LayerConfigRoute(LayerConfig cfg){
		this.cfg = cfg;
	}

	private final LayerConfig cfg;


	@Override
	public Object handle(Request request, Response response) throws Exception {
		response.header("Content-Type", "application/json");
		return cfg;
	}

}
