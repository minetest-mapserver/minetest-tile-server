package io.rudin.minetest.tileserver.route;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.rudin.minetest.tileserver.TileRenderer;
import io.rudin.minetest.tileserver.config.Layer;
import io.rudin.minetest.tileserver.config.LayerConfig;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.service.TileCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class TileRoute implements Route {

	private static final Logger logger = LoggerFactory.getLogger(TileRoute.class);

	@Inject
	public TileRoute(TileRenderer renderer, TileCache cache, LayerConfig layerConfig) {
		this.renderer = renderer;
		this.cache = cache;

		for (Layer layer: layerConfig.layers)
			layerMap.put(layer.id, layer);
	}

	private final Map<Integer, Layer> layerMap = new HashMap<>();

	private final TileRenderer renderer;

	private final TileCache cache;

	static final Histogram requestLatency = Histogram.build()
			.name("tileserver_requests_latency_seconds").help("Request latency in seconds.").register();

	static final Gauge activeEntries = Gauge.build()
			.name("tileserver_tile_route_entries")
			.help("Active tile route entries.")
			.register();

	static final Gauge outgoingBytesCached = Gauge.build()
			.name("tileserver_tile_route_bytes_cached")
			.help("Active tile route outgoing bytes (cached).")
			.register();

	static final Gauge outgoingBytesUncached = Gauge.build()
			.name("tileserver_tile_route_bytes_uncached")
			.help("Active tile route outgoing bytes (uncached).")
			.register();

	@Override
	public Object handle(Request req, Response res) throws Exception {
		res.header("Content-Type", "image/png");
		int z = Integer.parseInt(req.params(":z"));
		int y = Integer.parseInt(req.params(":y"));
		int x = Integer.parseInt(req.params(":x"));
		int layerid = Integer.parseInt(req.params(":layerid"));

		Histogram.Timer requestTimer = requestLatency.startTimer();
		activeEntries.inc();

		try {

			Layer layer = layerMap.get(layerid);

			if (layer == null)
				throw new IllegalArgumentException("layer not found: " + layerid);

			if (z < 2 || z > 12)
				throw new IllegalArgumentException("Invalid zoom: " + z);

			//check db cache
			byte[] tile = cache.get(layerid, x, y, z);
			if (tile != null) {
				logger.debug("Serving tile from cache @ {}/{} zoom: {}", x,y,z);
				outgoingBytesCached.inc(tile.length);
				return tile;
			}

			logger.debug("Rendering tile @ {}/{} zoom: {}", x,y,z);

			tile = renderer.render(layer,x, y, z);
			outgoingBytesUncached.inc(tile.length);
			return tile;

		} finally {
			requestTimer.observeDuration();
			activeEntries.dec();
		}
	}

}
