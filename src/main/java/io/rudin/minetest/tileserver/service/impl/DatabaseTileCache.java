package io.rudin.minetest.tileserver.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.tiledb.tables.records.TilesRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;

import static io.rudin.minetest.tileserver.tiledb.tables.Tiles.TILES;

@Singleton
public class DatabaseTileCache implements TileCache, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTileCache.class);

    @Inject
    public DatabaseTileCache(@TileDB DSLContext ctx, ScheduledExecutorService executor, TileServerConfig cfg){
        this.ctx = ctx;
        this.executor = executor;
        this.cfg = cfg;

        if (cfg.tileDatabaseAsync()) {
            //Initial run
            executor.submit(this);
        }
    }

    private final TileServerConfig cfg;

    private final ScheduledExecutorService executor;

    private final DSLContext ctx;

    private LinkedBlockingQueue<TilesRecord> tileInsertQueue = new LinkedBlockingQueue<>();

    @Override
    public void put(int x, int y, int z, byte[] data) {

        TilesRecord record = ctx
                .selectFrom(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .fetchOne();

        if (record == null) {
            //Insert
            record = ctx.newRecord(TILES);
            record.setX(x);
            record.setY(y);
            record.setZ(z);

        }

        //update
        record.setTile(data);
        record.setMtime(System.currentTimeMillis());

        if (cfg.tileDatabaseAsync()) {
            tileInsertQueue.add(record);

        } else {
            insertOrupdate(record);

        }
    }



    @Override
    public byte[] get(int x, int y, int z) {

        return ctx
                .select(TILES.TILE)
                .from(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .fetchOne(TILES.TILE);

    }


    @Override
    public boolean has(int x, int y, int z) {

        Integer count = ctx
                .select(DSL.count())
                .from(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .fetchOne(DSL.count());

        return count > 0;
    }



    @Override
    public void remove(int x, int y, int z) {

        long start = System.currentTimeMillis();

        ctx.delete(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .execute();

        long diff = System.currentTimeMillis() - start;
        if (diff > 500){
            logger.warn("Tile removal ({},{},{}) took {} ms", x,y,z, diff);
        }
    }

    @Override
    public long getLatestTimestamp() {
        return ctx
                .select(DSL.max(TILES.MTIME))
                .from(TILES)
                .fetchOne(DSL.max(TILES.MTIME));
    }

    @Override
    public void close() {

    }

    private void insertOrupdate(TilesRecord record){
        ctx
                .insertInto(TILES, record.fields())
                .values(record.intoArray())
                .onDuplicateKeyUpdate()
                .set(TILES.X, record.getX())
                .set(TILES.Y, record.getY())
                .set(TILES.Z, record.getZ())
                .set(TILES.MTIME, record.getMtime())
                .set(TILES.TILE, record.getTile())
                .execute();
    }

    @Override
    public void run() {

        try {
            while (true) {
                TilesRecord record = tileInsertQueue.take();
                insertOrupdate(record);
            }

        } catch (InterruptedException e) {
            logger.error("run", e);

        } catch (RuntimeException e){
            logger.error("run", e);

            //re-schedule
            executor.schedule(this, 500, TimeUnit.MILLISECONDS);
        }
    }
}
