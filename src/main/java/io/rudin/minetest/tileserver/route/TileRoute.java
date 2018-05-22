package io.rudin.minetest.tileserver.route;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.TileRenderer;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Singleton
public class TileRoute implements Route {

	@Inject
	public TileRoute(TileRenderer renderer, TileServerConfig cfg) {
		this.renderer = renderer;
		this.executorService = Executors.newFixedThreadPool(cfg.tilerendererProcesses());
	}

	private final TileRenderer renderer;

	private final ExecutorService executorService;

	@Override
	public Object handle(Request req, Response res) throws Exception {
		res.header("Content-Type", "image/png");
		int z = Integer.parseInt(req.params(":z"));
		int y = Integer.parseInt(req.params(":y"));
		int x = Integer.parseInt(req.params(":x"));

		//dispatch rendering to fixed pool
		Future<byte[]> future = executorService.submit(() -> renderer.render(x, y, z));

		return future.get();
	}

}
