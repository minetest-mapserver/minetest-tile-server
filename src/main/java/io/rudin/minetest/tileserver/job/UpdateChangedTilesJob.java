package io.rudin.minetest.tileserver.job;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.rudin.minetest.tileserver.TileRenderer;
import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.accessor.MapBlockAccessor;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.Layer;
import io.rudin.minetest.tileserver.config.LayerConfig;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.query.YQueryBuilder;
import io.rudin.minetest.tileserver.service.BlocksRecordService;
import io.rudin.minetest.tileserver.service.EventBus;
import io.rudin.minetest.tileserver.util.coordinate.CoordinateFactory;
import io.rudin.minetest.tileserver.util.coordinate.MapBlockCoordinate;
import io.rudin.minetest.tileserver.util.coordinate.TileCoordinate;
import org.jooq.*;

import io.rudin.minetest.tileserver.service.TileCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Singleton
public class UpdateChangedTilesJob implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(UpdateChangedTilesJob.class);

	static final Counter changedTileCounter = Counter.build()
			.name("tileserver_changed_tiles_total").help("Total changed tiles.").register();

	static final Gauge changedTiles = Gauge.build()
			.name("tileserver_changed_tiles")
			.help("Changed tiles in update job")
			.register();

	static final Map<Integer, Gauge> layerBlockChangeGaugeMap = new HashMap<>();

	static final Map<Integer, Gauge> layerBlockQueryTimingGaugeMap = new HashMap<>();


	@Inject
	public UpdateChangedTilesJob(@MapDB DSLContext ctx, TileCache tileCache, EventBus eventBus, TileServerConfig cfg,
								 MapBlockAccessor mapBlockAccessor, BlocksRecordService blocksRecordService,
								 TileRenderer tileRenderer, YQueryBuilder yQueryBuilder, LayerConfig layerCfg) {
		this.ctx = ctx;
		this.tileCache = tileCache;
		this.eventBus = eventBus;
		this.tileRenderer = tileRenderer;

		this.yQueryBuilder = yQueryBuilder;
		this.layerCfg = layerCfg;
		this.cfg = cfg;

		this.mapBlockAccessor = mapBlockAccessor;
		this.blocksRecordService = blocksRecordService;

		for (Layer layer: layerCfg.layers){
			layerBlockChangeGaugeMap.put(layer.id, Gauge.build()
					.name("tileserver_changed_blocks_layer_" + layer.id)
					.help("Changed blocks in update job for layer " + layer.name)
					.register()
			);
			layerBlockQueryTimingGaugeMap.put(layer.id, Gauge.build()
					.name("tileserver_changed_blocks_timing_layer_" + layer.id)
					.help("Changed blocks query timing in update job for layer " + layer.name)
					.register()
			);
		}
	}

	private final YQueryBuilder yQueryBuilder;

	private final LayerConfig layerCfg;

	private final TileRenderer tileRenderer;

	private final MapBlockAccessor mapBlockAccessor;

	private final BlocksRecordService blocksRecordService;

	private final TileServerConfig cfg;

	private final EventBus eventBus;

	private final DSLContext ctx;
	
	private final TileCache tileCache;

	private boolean running = false;

	private String getTileKey(TileCoordinate tile){
		return "Tile:" + tile.x + "/" + tile.y + "/" + tile.zoom;
	}

	static final Histogram changedTilesTime = Histogram.build()
			.name("tileserver_update_changed_tiles_time_seconds").help("Tile update job time in seconds.").register();

	private final Map<Integer, Long> timestampMap = new HashMap<>();



	@Override
	public void run() {
		//Run without parsing summary
		updateChangedTiles();
	}


	/**
	 * Execution summory of changed tiles
	 */
	public static class ChangedTilesResult {
		public Map<Integer, Integer> renderedTilesPerLayer = new HashMap<>();
		public Map<Integer, Integer> skippedTilesPerLayer = new HashMap<>();
		public Map<Integer, Integer> changedBlocksPerLayer = new HashMap<>();
		public double executionTime;

		@Override
		public String toString() {
			return "ChangedTilesResult{" +
					"renderedTilesPerLayer=" + renderedTilesPerLayer +
					", skippedTilesPerLayer=" + skippedTilesPerLayer +
					", changedBlocksPerLayer=" + changedBlocksPerLayer +
					", executionTime=" + executionTime +
					'}';
		}
	}

	/**
	 * Updates the changed tiles and returns a summary of all actions
	 * @return
	 */
	public ChangedTilesResult updateChangedTiles(){

		ChangedTilesResult result = new ChangedTilesResult();

		if (running) {
			//skip multiple invocations
			return result;
		}

		if (timestampMap.isEmpty()) {

			long latestTimestamp = 0L;

			if (cfg.tilerendererEnableInitialRendering()){
				logger.info("Initial rendering detected, ignoring updated blocks since now");
				latestTimestamp = System.currentTimeMillis();

			} else {

				logger.debug("Gathering latest tile time from tile cache");
				long start = System.currentTimeMillis();

				latestTimestamp = tileCache.getLatestTimestamp();

				long diff = System.currentTimeMillis() - start;

				logger.info("Newest tile time is {} / {}", latestTimestamp, new Date(latestTimestamp));

				if (diff > 1000){
					logger.warn("Tile time fetch took {} ms", diff);
				}
			}

			for (Layer layer: layerCfg.layers){
				timestampMap.put(layer.id, latestTimestamp);
			}

		}

		Histogram.Timer timer = null;
		long tileCount = 0;

		try {

			timer = changedTilesTime.startTimer();

			long start = System.currentTimeMillis();

			running = true;
			final int LIMIT = cfg.tilerendererUpdateMaxBlocks();

			for (Layer layer: layerCfg.layers) {

				long latestTimestamp = timestampMap.get(layer.id);

				logger.debug("update for layer: {}", layer.name);

				long t0 = System.currentTimeMillis();

				Condition yCondition = yQueryBuilder.getCondition(layer);

				Result<BlocksRecord> blocks = ctx
						.selectFrom(BLOCKS)
						.where(BLOCKS.MTIME.gt(latestTimestamp))
						.and(yCondition)
						.orderBy(BLOCKS.MTIME.asc()) //oldest first
						.limit(LIMIT)
						.fetch();

				long queryTime = System.currentTimeMillis() - t0;

				int count = blocks.size();
				int invalidatedTiles = 0;

				//internal summary
				result.changedBlocksPerLayer.put(layer.id, count);

				//prometheus
				layerBlockQueryTimingGaugeMap.get(layer.id).set(queryTime);
				layerBlockChangeGaugeMap.get(layer.id).set(count);

				long diff = System.currentTimeMillis() - start;

				if (diff > 500 && cfg.logQueryPerformance()) {
					logger.warn("updated-tiles-query took {} ms", diff);
				} else {
					logger.debug("updated-tiles-query took {} ms", diff);
				}

				if (blocks.size() == LIMIT) {
					logger.warn("Got max-blocks ({}) from update-queue", LIMIT);
				}

				logger.debug("Got {} updated blocks", blocks.size());

				for (BlocksRecord record : blocks) {
					blocksRecordService.update(record);
					mapBlockAccessor.invalidate(new Coordinate(record));
				}

				//assign new timestamp
				timestampMap.put(layer.id, latestTimestamp);


				logger.debug("Starting rendering of changed tiles");

				int changedTileCount = 0;
				int skippedTileCount = 0;

				List<String> updatedTileKeys = new ArrayList<>();

				//run with rendering of other zoom levels, but cached
				for (BlocksRecord record : blocks) {

					Integer x = record.getPosx();
					Integer z = record.getPosz();

					TileCoordinate tileCoordinate = CoordinateFactory.getTileCoordinateFromMapBlock(new MapBlockCoordinate(x, z));

					for (int i = CoordinateFactory.MAX_ZOOM; i >= 3; i--) {

						String tileKey = getTileKey(tileCoordinate);

						if (!updatedTileKeys.contains(tileKey)) {

							//Generate tiles now
							logger.debug("Rendering tile x={} y={} zoom={}", tileCoordinate.x, tileCoordinate.y, tileCoordinate.zoom);
							tileRenderer.render(layer, tileCoordinate.x, tileCoordinate.y, tileCoordinate.zoom);

							logger.debug("Dispatching tile-changed-event for tile: {}/{}", tileCoordinate.x, tileCoordinate.y);

							EventBus.TileChangedEvent event = new EventBus.TileChangedEvent();
							event.layerId = layer.id;
							event.x = tileCoordinate.x;
							event.y = tileCoordinate.y;
							event.zoom = tileCoordinate.zoom;
							event.mapblockX = x;
							event.mapblockZ = z;
							eventBus.post(event);

							changedTileCount++;
							updatedTileKeys.add(tileKey);

						} else {
							skippedTileCount++;

						}

						//zom out
						tileCoordinate = CoordinateFactory.getZoomedOutTile(tileCoordinate);

					}

				}

				result.renderedTilesPerLayer.put(layer.id, changedTileCount);
				result.skippedTilesPerLayer.put(layer.id, skippedTileCount);

				final String msg = "Tile update job took {} ms for {} blocks in layer: '{}' (invalidated {} tiles)";
				final Object[] params = new Object[]{
						System.currentTimeMillis() - start,
						count,
						layer.name,
						invalidatedTiles
				};

				if (cfg.logTileUpdateTimings())
					logger.info(msg, params);
				else
					logger.debug(msg, params);
			}

		} catch(Exception e){
			logger.error("tile-updater", e);

		} finally {
			changedTiles.set(tileCount);

			running = false;
			result.executionTime = timer.observeDuration();
		}

		return result;
	}

}
