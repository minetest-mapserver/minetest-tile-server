package io.rudin.minetest.tileserver.job;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;
import static io.rudin.minetest.tileserver.blockdb.tables.TileserverTiles.TILESERVER_TILES;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.*;

import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.util.CoordinateResolver;
import io.rudin.minetest.tileserver.util.CoordinateResolver.TileInfo;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

@Singleton
public class UpdateChangedTilesJob implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(UpdateChangedTilesJob.class);

	@Inject
	public UpdateChangedTilesJob(DSLContext ctx, TileCache tileCache, EventBus eventBus, TileServerConfig cfg) {
		this.ctx = ctx;
		this.tileCache = tileCache;
		this.eventBus = eventBus;

		this.yCondition = BLOCKS.POSY.between(cfg.tilesMinY(), cfg.tilesMaxY());
	}

	private final EventBus eventBus;

	private final DSLContext ctx;
	
	private final TileCache tileCache;

	private boolean running = false;

	private Long latestTimestamp = null;

	private final Condition yCondition;

	@Override
	public void run() {

		if (running) {
			//skip multiple invocations
			return;
		}

		if (latestTimestamp == null) {
			logger.debug("Gathering latest tile time from db");
			long start = System.currentTimeMillis();

			latestTimestamp = ctx
					.select(DSL.max(TILESERVER_TILES.MTIME))
					.from(TILESERVER_TILES)
					.fetchOne(DSL.max(TILESERVER_TILES.MTIME));

			long diff = System.currentTimeMillis() - start;

			logger.debug("Newest tile time is {}", latestTimestamp);

			if (diff > 1000){
				logger.warn("Tile time fetch took {} ms", diff);
			}
		}


		try {
			running = true;

			Result<BlocksRecord> blocks = ctx
					.selectFrom(BLOCKS)
					.where(BLOCKS.MTIME.ge(latestTimestamp))
					.and(yCondition)
					.orderBy(BLOCKS.MTIME.asc()) //oldest first
					.limit(200)
					.fetch();

			if (blocks.size() == 200){
				logger.warn("Got max-blocks from update-queue");
			}

			for (BlocksRecord record: blocks) {
				Integer x = record.getPosx();
				Integer z = record.getPosz();

				if (record.getMtime() > latestTimestamp){
					//Update timestamp
					latestTimestamp = record.getMtime();
				}

				TileInfo tileInfo = CoordinateResolver.fromCoordinates(x, z);

				//remove all tiles in every zoom
				for (int i=CoordinateResolver.MAX_ZOOM; i>=CoordinateResolver.MIN_ZOOM; i--) {
					TileInfo zoomedTile = tileInfo.toZoom(i);

					EventBus.TileChangedEvent event = new EventBus.TileChangedEvent();
					event.x = zoomedTile.x;
					event.y = zoomedTile.y;
					event.zoom = zoomedTile.zoom;
					event.mapblockX = x;
					event.mapblockZ = z;
					eventBus.post(event);

					tileCache.remove(zoomedTile.x, zoomedTile.y, zoomedTile.zoom);
				}


			}


		} finally {
			running = false;

		}

	}

}
