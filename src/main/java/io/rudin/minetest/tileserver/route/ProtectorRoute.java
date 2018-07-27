package io.rudin.minetest.tileserver.route;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Protector;
import io.rudin.minetest.tileserver.util.CoordinateResolver;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.inject.Singleton;

import static io.rudin.minetest.tileserver.blockdb.tables.Protector.PROTECTOR;

@Singleton
public class ProtectorRoute implements Route {

	private static final Logger logger = LoggerFactory.getLogger(ProtectorRoute.class);

	@Inject
	public ProtectorRoute(DSLContext ctx) {
		this.ctx = ctx;
	}

	private final DSLContext ctx;


	@Override
	public Object handle(Request req, Response res) throws Exception {
		res.header("Content-Type", "application/json");
		int z = Integer.parseInt(req.params(":z"));
		int y = Integer.parseInt(req.params(":y"));
		int x = Integer.parseInt(req.params(":x"));

		CoordinateResolver.MapBlockCoordinateInfo info = CoordinateResolver.fromTile(x, y, z);

		System.out.println(info);//XXX

		return ctx
				.selectFrom(PROTECTOR)
				.where(PROTECTOR.POSX.ge(info.x))
				.and(PROTECTOR.POSX.le((int) (info.x + info.width)))
				.and(PROTECTOR.POSZ.ge(info.z))
				.and(PROTECTOR.POSZ.le((int)(info.z + info.height)))
				.fetchInto(Protector.class);

	}

}
