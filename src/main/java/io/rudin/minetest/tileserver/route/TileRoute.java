package io.rudin.minetest.tileserver.route;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.TileRenderer;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.service.TileCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Singleton
public class TileRoute implements Route {

	private static final Logger logger = LoggerFactory.getLogger(TileRoute.class);

	@Inject
	public TileRoute(TileRenderer renderer, TileServerConfig cfg, TileCache cache) {
		this.renderer = renderer;
		this.executorService = Executors.newFixedThreadPool(cfg.tilerendererProcesses());
		this.cache = cache;
	}

	private final TileRenderer renderer;

	private final TileCache cache;

	private final ExecutorService executorService;

	@Override
	public Object handle(Request req, Response res) throws Exception {
		res.header("Content-Type", "image/png");
		int z = Integer.parseInt(req.params(":z"));
		int y = Integer.parseInt(req.params(":y"));
		int x = Integer.parseInt(req.params(":x"));

		if (cache.has(x,y,z)) {
			//check db cache
			logger.debug("Serving tile from cache @ {}/{} zoom: {}", x,y,z);
			return cache.get(x,y,z);
		}

		logger.debug("Rendering tile @ {}/{} zoom: {}", x,y,z);

		//dispatch rendering to fixed pool
		Future<byte[]> future = executorService.submit(() -> renderer.render(x, y, z));

		return future.get();
	}

}
