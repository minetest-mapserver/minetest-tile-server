package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import org.aeonbits.owner.ConfigFactory;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.impl.DSL;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import io.rudin.minetest.tileserver.util.CoordinateResolver;
import io.rudin.minetest.tileserver.util.CoordinateResolver.TileInfo;

public class StaticTileGen {

	public static void main(String[] args) throws Exception {
		// TODO create tiles in directory with static html index file

		TileServerConfig cfg = ConfigFactory.create(TileServerConfig.class);

		Injector injector = Guice.createInjector(
				new ConfigModule(cfg),
				new DBModule(cfg),
				new ServiceModule(cfg)
				);

		DSLContext ctx = injector.getInstance(DSLContext.class);
		TileRenderer tileRenderer = injector.getInstance(TileRenderer.class);

		Condition yCondition = BLOCKS.POSY.between(cfg.tilesMinY(), cfg.tilesMaxY());

		Record2<Integer, Integer> result = ctx
				.select(DSL.max(BLOCKS.POSX), DSL.min(BLOCKS.POSX))
				.from(BLOCKS)
				.where(yCondition)
				.fetchOne();

		Integer maxX = result.get(DSL.max(BLOCKS.POSX));
		Integer minX = result.get(DSL.min(BLOCKS.POSX));

		System.out.println("Max-X " + maxX + " Min-X: " + minX);

		for (int zoom=CoordinateResolver.MAX_ZOOM; zoom>=CoordinateResolver.MIN_ZOOM; zoom--) {

			int factor = (int)Math.pow(2, CoordinateResolver.ONE_TO_ONE_ZOOM - zoom);
			int step = 16 * factor;
			
			for (int x=minX; x<=maxX; x+=step) {

				Record2<Integer, Integer> zrange = ctx
						.select(DSL.max(BLOCKS.POSZ), DSL.min(BLOCKS.POSZ))
						.from(BLOCKS)
						.where(BLOCKS.POSX.eq(x))
						.and(yCondition)
						.fetchOne();

				Integer maxZ = zrange.get(DSL.max(BLOCKS.POSZ));
				Integer minZ = zrange.get(DSL.min(BLOCKS.POSZ));

				if (maxZ == null || minZ == null)
					continue;

				for (int z=minZ; z<=maxZ; z+=step) {

					TileInfo tileInfo = CoordinateResolver.fromCoordinates(x, z).toZoom(zoom);

					//TileInfo minZoom = tileInfo.toZoom(CoordinateResolver.MIN_ZOOM);


					long start = System.currentTimeMillis();
					tileRenderer.render(tileInfo.x, tileInfo.y, tileInfo.zoom);
					long diff = System.currentTimeMillis() - start;

					System.out.println("Tile: " + tileInfo.x + "/" + tileInfo.y + " @ " + tileInfo.zoom + " took " + diff + " ms");

				}
			}
		}


	}

}
