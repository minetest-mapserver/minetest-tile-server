package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;

import io.prometheus.client.Histogram;
import io.rudin.minetest.tileserver.config.Layer;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.query.YQueryBuilder;
import io.rudin.minetest.tileserver.service.MapBlockRenderService;
import io.rudin.minetest.tileserver.util.coordinate.*;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.util.WhiteTile;

@Singleton
public class TileRenderer {

	private static final Logger logger = LoggerFactory.getLogger(TileRenderer.class);

	public static final int TILE_PIXEL_SIZE = 256;

	@Inject
	public TileRenderer(@MapDB DSLContext ctx, TileCache cache, MapBlockRenderService blockRenderer, TileServerConfig cfg, YQueryBuilder yQueryBuilder) {
		this.ctx = ctx;
		this.cache = cache;
		this.blockRenderer = blockRenderer;
		this.cfg = cfg;
		this.yQueryBuilder = yQueryBuilder;

		ImageIO.setUseCache(false);
	}

	private final YQueryBuilder yQueryBuilder;

	private final TileServerConfig cfg;

	private final TileCache cache;

	private final DSLContext ctx;

	private final MapBlockRenderService blockRenderer;

	static final Histogram renderTime = Histogram.build()
			.name("tileserver_render_time_seconds").help("Render time in seconds.").register();

	public BufferedImage createTile() {
		return new BufferedImage(TILE_PIXEL_SIZE, TILE_PIXEL_SIZE, BufferedImage.TYPE_INT_RGB);
	}

	private final int DEFAULT_BLOCK_ZOOM = 13;

	public byte[] render(Layer layer, int tileX, int tileY, int zoom) throws IllegalArgumentException, DataFormatException, IOException, ExecutionException {
		return render(layer, tileX, tileY, zoom, true);
	}

	public byte[] render(Layer layer, int tileX, int tileY, int zoom, boolean usecache) throws IllegalArgumentException, DataFormatException, IOException, ExecutionException {

		//Check binary cache
		if (usecache && cache.has(layer.id, tileX, tileY, zoom)) {
			byte[] tile =  cache.get(layer.id, tileX, tileY, zoom);

			if (tile == null || tile.length == 0){
				logger.error("Got a null/zero tile @ {}/{}/{}", tileX, tileY, zoom);
			}
		}

		Range<MapBlockCoordinate> mapBlocksRange = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(tileX, tileY, zoom));

		int max = Math.max(
				Math.max(mapBlocksRange.pos1.x, mapBlocksRange.pos1.z),
				Math.max(mapBlocksRange.pos2.x, mapBlocksRange.pos2.x)
		);

		int min = Math.min(
				Math.min(mapBlocksRange.pos1.x, mapBlocksRange.pos1.z),
				Math.min(mapBlocksRange.pos2.x, mapBlocksRange.pos2.x)
		);

		if (max > 2048 || min < -2048)
			//Out of range...
			return WhiteTile.getPNG();

		
		if (zoom < DEFAULT_BLOCK_ZOOM) {
			//TODO: fail-fast for regions without map-blocks -> white

			long start = System.currentTimeMillis();

			Result<BlocksRecord> firstResult = ctx
					.selectFrom(BLOCKS)
					.where(
							BLOCKS.POSX.ge(Math.min(mapBlocksRange.pos1.x, mapBlocksRange.pos2.x))
							.and(BLOCKS.POSX.le(Math.max(mapBlocksRange.pos1.x, mapBlocksRange.pos2.x)))
							.and(BLOCKS.POSZ.ge(Math.min(mapBlocksRange.pos1.z, mapBlocksRange.pos2.z)))
							.and(BLOCKS.POSZ.le(Math.max(mapBlocksRange.pos1.z, mapBlocksRange.pos2.z)))
							.and(yQueryBuilder.getCondition(layer))
					)
					.limit(1)
					.fetch();

			long diff = System.currentTimeMillis() - start;

			if (diff > 250 && cfg.logQueryPerformance()){
				logger.warn("white-count-query took {} ms", diff);
			}
		
			if (firstResult.isEmpty()) {
				logger.debug("Fail-fast, got zero mapblock count for x=({})-({}) z=({})-({})",
						mapBlocksRange.pos1.x, mapBlocksRange.pos2.x,
						mapBlocksRange.pos1.z, mapBlocksRange.pos2.z);

				byte[] data = WhiteTile.getPNG();

				if (zoom < 11) {
					//Only cache white space in upper zoom levels
					cache.put(layer.id, tileX, tileY, zoom, data);
				}

				return data;
			}
			
		}


		BufferedImage image = renderImage(layer, tileX, tileY, zoom, usecache);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "png", output);

		byte[] data = output.toByteArray();

		return data;

	}

	/**
	 * Render the actual image to a bufferedImage
	 * @param tileX
	 * @param tileY
	 * @param zoom
	 * @return
	 * @throws IllegalArgumentException
	 * @throws DataFormatException
	 * @throws IOException
	 * @throws ExecutionException
	 */
	public BufferedImage renderImage(Layer layer, int tileX, int tileY, int zoom) throws IllegalArgumentException, DataFormatException, IOException, ExecutionException {
		return renderImage(layer, tileX, tileY, zoom, true);
	}

	public BufferedImage renderImage(Layer layer, int tileX, int tileY, int zoom, boolean usecache) throws IllegalArgumentException, DataFormatException, IOException, ExecutionException {

		//Check if binary cached, use cached version for rendering
		if (usecache && cache.has(layer.id, tileX, tileY, zoom)) {
			byte[] data = cache.get(layer.id, tileX, tileY, zoom);

			if (data != null && data.length > 0)
				//In case the cache disappears
				return ImageIO.read(new ByteArrayInputStream(data));
		}

		Histogram.Timer timer = renderTime.startTimer();

		try {

			final int HALF_TILE_PIXEL_SIZE = TILE_PIXEL_SIZE / 2;


			if (zoom < DEFAULT_BLOCK_ZOOM) {
				//Zoom out


				BufferedImage tile = createTile();
				//Pack 4 tiles from higher zoom into 1 tile

				TileQuadrants quadrants = CoordinateFactory.getZoomedQuadrantsFromTile(new TileCoordinate(tileX, tileY, zoom));

				int nextZoom = zoom + 1;
				int nextZoomX = tileX * 2;
				int nextZoomY = tileY * 2;

				BufferedImage upperLeft = renderImage(layer, quadrants.upperLeft.x, quadrants.upperLeft.y, quadrants.upperLeft.zoom);
				BufferedImage upperRightImage = renderImage(layer, quadrants.upperRight.x, quadrants.upperRight.y, quadrants.upperRight.zoom);
				BufferedImage lowerLeftImage = renderImage(layer, quadrants.lowerLeft.x, quadrants.lowerLeft.y, quadrants.lowerLeft.zoom);
				BufferedImage lowerRightImage = renderImage(layer, quadrants.lowerRight.x, quadrants.lowerRight.y, quadrants.lowerRight.zoom);

				long start = System.currentTimeMillis();

				Graphics2D graphics = tile.createGraphics();

				Image upperLeftScaledInstance = upperLeft.getScaledInstance(HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, Image.SCALE_FAST);
				graphics.drawImage(upperLeftScaledInstance, 0, 0, HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, null);

				Image upperRightScaledInstance = upperRightImage.getScaledInstance(HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, Image.SCALE_FAST);
				graphics.drawImage(upperRightScaledInstance, HALF_TILE_PIXEL_SIZE, 0, HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, null);

				Image lowerLeftScaledInstance = lowerLeftImage.getScaledInstance(HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, Image.SCALE_FAST);
				graphics.drawImage(lowerLeftScaledInstance, 0, HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, null);

				Image lowerRightScaledInstance = lowerRightImage.getScaledInstance(HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, Image.SCALE_FAST);
				graphics.drawImage(lowerRightScaledInstance, HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, HALF_TILE_PIXEL_SIZE, null);

				ByteArrayOutputStream output = new ByteArrayOutputStream(12000);
				ImageIO.write(tile, "png", output);

				byte[] data = output.toByteArray();

				long diff = System.currentTimeMillis() - start;

				logger.trace("Timings of cross-stitched tile X={} Y={} Zoom={}: render={} ms", tileX, tileY, zoom, diff);

				cache.put(layer.id, tileX, tileY, zoom, data);

				if (tile == null)
					logger.error("Got a null-tile @ {}/{}/{} (data={})", tileX, tileY, zoom, data.length);

				return tile;

			}

			//Default zoom (13 == 1 mapblock with 16px wide blocks)
			long start = System.currentTimeMillis();

			Range<MapBlockCoordinate> coordinateRange = CoordinateFactory.getMapBlocksInTile(new TileCoordinate(tileX, tileY, zoom));

			//16x16 mapblocks on a tile
			BufferedImage image = createTile();
			Graphics2D graphics = image.createGraphics();

			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, TILE_PIXEL_SIZE, TILE_PIXEL_SIZE);


			int mapblockX = coordinateRange.pos1.x;
			int mapblockZ = coordinateRange.pos1.z;

			long now = System.currentTimeMillis();
			long timingImageSetup = now - start;
			start = now;


			Result<BlocksRecord> countList = ctx
					.selectFrom(BLOCKS)
					.where(
							BLOCKS.POSX.eq(mapblockX)
									.and(BLOCKS.POSZ.eq(mapblockZ))
									.and(yQueryBuilder.getCondition(layer))
					)
					.limit(1)
					.fetch();

			now = System.currentTimeMillis();
			long timingZeroCountCheck = now - start;

			if (timingZeroCountCheck > 250 && cfg.logQueryPerformance()) {
				logger.warn("count-zero-check took {} ms", timingZeroCountCheck);
			}

			start = now;

			long timingRender = 0;

			if (!countList.isEmpty()) {
				logger.debug("Rendering tile for mapblock: X={}, Z={}", mapblockX, mapblockZ);
				blockRenderer.render(layer.from, layer.to, mapblockX, mapblockZ, graphics, 16);

				now = System.currentTimeMillis();
				timingRender = now - start;
				start = now;
			}

			ByteArrayOutputStream output = new ByteArrayOutputStream(12000);
			ImageIO.write(image, "png", output);

			byte[] data = output.toByteArray();

			now = System.currentTimeMillis();
			long timingBufferOutput = now - start;


			logger.trace("Timings of tile X={} Y={} Zoom={}: setup={} ms, zeroCheck={} ms, render={} ms, output={} ms",
					tileX, tileY, zoom,
					timingImageSetup, timingZeroCountCheck, timingRender, timingBufferOutput
			);

			cache.put(layer.id, tileX, tileY, zoom, data);

			if (image == null)
				logger.error("Got a null-tile @ {}/{}/{} (layer={},data={})", tileX, tileY, zoom, layer.id, data.length);

			return image;

		} finally {
			timer.observeDuration();

		}
	}

}
