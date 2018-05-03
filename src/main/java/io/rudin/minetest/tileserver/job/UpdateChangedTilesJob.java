package io.rudin.minetest.tileserver.job;

import static io.rudin.minetest.tileserver.blockdb.tables.TileserverBlockChanges.TILESERVER_BLOCK_CHANGES;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record2;

import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.util.CoordinateResolver;
import io.rudin.minetest.tileserver.util.CoordinateResolver.TileInfo;

@Singleton
public class UpdateChangedTilesJob implements Runnable {

	@Inject
	public UpdateChangedTilesJob(DSLContext ctx, TileCache tileCache, EventBus eventBus) {
		this.ctx = ctx;
		this.tileCache = tileCache;
		this.eventBus = eventBus;
	}

	private final EventBus eventBus;

	private final DSLContext ctx;
	
	private final TileCache tileCache;

	private boolean running = false;

	@Override
	public void run() {

		if (running) {
			//skip multiple invocations
			return;
		}

		try {
			running = true;


			Cursor<Record2<Integer, Integer>> cursor = ctx
					.select(TILESERVER_BLOCK_CHANGES.POSX, TILESERVER_BLOCK_CHANGES.POSZ)
					.from(TILESERVER_BLOCK_CHANGES)
					.where(TILESERVER_BLOCK_CHANGES.CHANGED.eq(true))
					.groupBy(TILESERVER_BLOCK_CHANGES.POSZ, TILESERVER_BLOCK_CHANGES.POSX)
					.fetchLazy();
			try {
				
				for (Record2<Integer, Integer> change: cursor) {
					Integer x = change.get(TILESERVER_BLOCK_CHANGES.POSX);
					Integer z = change.get(TILESERVER_BLOCK_CHANGES.POSZ);
					

					TileInfo tileInfo = CoordinateResolver.fromCoordinatesMinZoom(x, z);
					
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

					//TODO: atomic change
					ctx
					.update(TILESERVER_BLOCK_CHANGES)
					.set(TILESERVER_BLOCK_CHANGES.CHANGED, false)
					.where(TILESERVER_BLOCK_CHANGES.POSX.eq(x))
					.and(TILESERVER_BLOCK_CHANGES.POSZ.eq(z))
					.execute();
				}
				
			} finally {
				cursor.close();

			}

		} finally {
			running = false;

		}

	}

}
