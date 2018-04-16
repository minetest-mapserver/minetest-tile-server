package io.rudin.minetest.tileserver.route;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.TileRenderer;
import spark.Request;
import spark.Response;
import spark.Route;

@Singleton
public class TileRoute implements Route {

	@Inject
	public TileRoute(TileRenderer renderer) {
		this.renderer = renderer;
	}

	private final TileRenderer renderer;

	@Override
	public Object handle(Request req, Response res) throws Exception {
		res.header("Content-Type", "image/png");
		int z = Integer.parseInt(req.params(":z"));
		int y = Integer.parseInt(req.params(":y"));
		int x = Integer.parseInt(req.params(":x"));

		return renderer.render(x, y, z);
	}

}
