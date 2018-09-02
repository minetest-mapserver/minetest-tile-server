package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Protector;
import io.rudin.minetest.tileserver.config.LayerConfig;
import io.rudin.minetest.tileserver.config.Layer;
import io.rudin.minetest.tileserver.util.CoordinateResolver;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.rudin.minetest.tileserver.blockdb.tables.Protector.PROTECTOR;

@Singleton
public class ProtectorRoute implements Route {

	private static final Logger logger = LoggerFactory.getLogger(ProtectorRoute.class);

	@Inject
	public ProtectorRoute(DSLContext ctx, LayerConfig layerCfg) {
		this.ctx = ctx;

		for (Layer layer: layerCfg.layers)
			layerMap.put(layer.id, layer);
	}

	private final DSLContext ctx;

	private final Map<Integer, Layer> layerMap = new HashMap<>();

	@Override
	public Object handle(Request req, Response res) throws Exception {
		res.header("Content-Type", "application/json");
		int z = Integer.parseInt(req.params(":z"));
		int x = Integer.parseInt(req.params(":x"));
		int layerId = Integer.parseInt(req.params(":layerId"));

		Layer layer = layerMap.get(layerId);

		if (layer == null)
			throw new IllegalArgumentException("layer not found: " + layerId);


		final int RANGE = 100;

		return ctx
				.selectFrom(PROTECTOR)
				.where(PROTECTOR.X.ge(x-RANGE))
				.and(PROTECTOR.X.le(x+RANGE))
				.and(PROTECTOR.Z.ge(z-RANGE))
				.and(PROTECTOR.Z.le(z+RANGE))
				.and(PROTECTOR.Y.ge(layer.from))
				.and(PROTECTOR.Y.le(layer.to))
				.fetchInto(Protector.class);

	}

}
