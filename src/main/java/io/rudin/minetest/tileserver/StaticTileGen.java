package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

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

		Injector injector = Guice.createInjector(
				new ConfigModule(),
				new DBModule(),
				new ServiceModule()
				);

		DSLContext ctx = injector.getInstance(DSLContext.class);
		TileRenderer tileRenderer = injector.getInstance(TileRenderer.class);

		Record2<Integer, Integer> result = ctx
				.select(DSL.max(BLOCKS.POSX), DSL.min(BLOCKS.POSX))
				.from(BLOCKS)
				.fetchOne();

		Integer maxX = result.get(DSL.max(BLOCKS.POSX));
		Integer minX = result.get(DSL.min(BLOCKS.POSX));

		System.out.println("Max-X " + maxX + " Min-X: " + minX);

		for (int x=minX; x<=maxX; x++) {

			Record2<Integer, Integer> zrange = ctx
					.select(DSL.max(BLOCKS.POSZ), DSL.min(BLOCKS.POSZ))
					.from(BLOCKS)
					.where(BLOCKS.POSX.eq(x))
					.fetchOne();

			Integer maxZ = zrange.get(DSL.max(BLOCKS.POSZ));
			Integer minZ = zrange.get(DSL.min(BLOCKS.POSZ));

			if (maxZ == null || minZ == null)
				continue;
			
			for (int z=minZ; z<=maxZ; z++) {
				
				TileInfo tileInfo = CoordinateResolver.fromCoordinates(x, z);
				TileInfo minZoom = tileInfo.toZoom(CoordinateResolver.MIN_ZOOM);
				
				System.out.println("Tile: " + minZoom.x + "/" + minZoom.y + " @ " + minZoom.zoom);
				
				tileRenderer.render(minZoom.x, minZoom.y, minZoom.zoom);
			}
			

		}



	}

}
