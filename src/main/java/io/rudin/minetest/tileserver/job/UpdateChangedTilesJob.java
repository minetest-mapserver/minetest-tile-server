package io.rudin.minetest.tileserver.job;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;
import static io.rudin.minetest.tileserver.tiledb.tables.Tiles.TILES;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.accessor.BlocksRecordAccessor;
import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.accessor.MapBlockAccessor;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.*;

import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.util.CoordinateResolver;
import io.rudin.minetest.tileserver.util.CoordinateResolver.TileInfo;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class UpdateChangedTilesJob implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(UpdateChangedTilesJob.class);

	@Inject
	public UpdateChangedTilesJob(DSLContext ctx, TileCache tileCache, EventBus eventBus, TileServerConfig cfg, MapBlockAccessor mapBlockAccessor, BlocksRecordAccessor blocksRecordAccessor) {
		this.ctx = ctx;
		this.tileCache = tileCache;
		this.eventBus = eventBus;

		this.yCondition = BLOCKS.POSY.between(cfg.tilesMinY(), cfg.tilesMaxY());
		this.cfg = cfg;

		this.mapBlockAccessor = mapBlockAccessor;
		this.blocksRecordAccessor = blocksRecordAccessor;
	}

	private final MapBlockAccessor mapBlockAccessor;

	private final BlocksRecordAccessor blocksRecordAccessor;

	private final TileServerConfig cfg;

	private final EventBus eventBus;

	private final DSLContext ctx;
	
	private final TileCache tileCache;

	private boolean running = false;

	private Long latestTimestamp = null;

	private final Condition yCondition;

	private String getTileKey(TileInfo tile){
		return "Tile:" + tile.x + "/" + tile.y + "/" + tile.zoom;
	}

	@Override
	public void run() {

		if (running) {
			//skip multiple invocations
			return;
		}

		if (latestTimestamp == null) {


			if (cfg.tilerendererEnableInitialRendering()){
				logger.info("Initial rendering detected, ignoring updated blocks since now");
				latestTimestamp = System.currentTimeMillis();

			} else {

				logger.debug("Gathering latest tile time from tile cache");
				long start = System.currentTimeMillis();

				latestTimestamp = tileCache.getLatestTimestamp();

				long diff = System.currentTimeMillis() - start;

				logger.debug("Newest tile time is {}", latestTimestamp);

				if (diff > 1000){
					logger.warn("Tile time fetch took {} ms", diff);
				}
			}

		}

		try {

			long start = System.currentTimeMillis();


			running = true;
			final int LIMIT = cfg.tilerendererUpdateMaxBlocks();

			Result<BlocksRecord> blocks = ctx
					.selectFrom(BLOCKS)
					.where(BLOCKS.MTIME.gt(latestTimestamp))
					.and(yCondition)
					.orderBy(BLOCKS.MTIME.asc()) //oldest first
					.limit(LIMIT)
					.fetch();

			int count = blocks.size();
			int invalidatedTiles = 0;

			long diff = start - System.currentTimeMillis();

			if (diff > 1000 && cfg.logQueryPerformance()){
				logger.warn("updated-tiles-query took {} ms", diff);
			}

			if (blocks.size() == LIMIT) {
				logger.warn("Got max-blocks ({}) from update-queue", LIMIT);
			}

			logger.debug("Got {} updated blocks", blocks.size());

			List<String> updatedTileKeys = new ArrayList<>();

			for (BlocksRecord record : blocks) {

				blocksRecordAccessor.update(record);
				mapBlockAccessor.invalidate(new Coordinate(record));

				Integer x = record.getPosx();
				Integer z = record.getPosz();

				if (record.getMtime() > latestTimestamp) {
					//Update timestamp
					latestTimestamp = record.getMtime();
				}

				TileInfo tileInfo = CoordinateResolver.fromCoordinates(x, z);

				//remove all tiles in every zoom
				for (int i = CoordinateResolver.MAX_ZOOM; i >= CoordinateResolver.MIN_ZOOM; i--) {
					TileInfo zoomedTile = tileInfo.toZoom(i);
					String tileKey = getTileKey(zoomedTile);

					if (!updatedTileKeys.contains(tileKey)) {
						invalidatedTiles++;
						tileCache.remove(zoomedTile.x, zoomedTile.y, zoomedTile.zoom);

						EventBus.TileChangedEvent event = new EventBus.TileChangedEvent();
						event.x = zoomedTile.x;
						event.y = zoomedTile.y;
						event.zoom = zoomedTile.zoom;
						event.mapblockX = x;
						event.mapblockZ = z;
						eventBus.post(event);

						updatedTileKeys.add(tileKey);
					}

				}


			}

			final String msg = "Tile update job took {} ms for {} blocks (invalidated {} tiles)";
			final Object[] params = new Object[]{
					System.currentTimeMillis()-start,
					count,
					invalidatedTiles
			};

			if (cfg.logTileUpdateTimings())
				logger.info(msg, params);
			else
				logger.debug(msg, params);

		} catch(Exception e){
			logger.error("tile-updater", e);

		} finally {
			running = false;

		}

	}

}
