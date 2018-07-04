package io.rudin.minetest.tileserver.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.Striped;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.tiledb.tables.records.TilesRecord;
import io.rudin.minetest.tileserver.util.MapBlockAccessor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static io.rudin.minetest.tileserver.tiledb.tables.Tiles.TILES;

@Singleton
public class DatabaseTileCache implements TileCache, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTileCache.class);

    @Inject
    public DatabaseTileCache(@TileDB DSLContext ctx, ScheduledExecutorService executor){
        this.ctx = ctx;
        this.executor = executor;

        //Initial run
        executor.submit(this);

        this.cache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .weakValues()
                .maximumSize(500)
                .build();
    }

    private final ScheduledExecutorService executor;

    private final Cache<String, byte[]> cache;

    private final DSLContext ctx;

    private String getKey(int x, int y, int z){
        return x + "/" + y + "/" + z;
    }

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

        tileInsertQueue.add(record);
    }



    @Override
    public byte[] get(int x, int y, int z) {

        String key = getKey(x, y, z);
        byte[] data = cache.getIfPresent(key);

        if (data != null)
            return data;

        return ctx
                .select(TILES.TILE)
                .from(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .fetchOne(TILES.TILE);

    }


    public boolean has(int x, int y, int z, boolean useCache) {

        if (useCache) {
            String key = getKey(x, y, z);
            byte[] data = cache.getIfPresent(key);

            if (data != null)
                return true;
        }

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
    public boolean has(int x, int y, int z) {
        return has(x,y,z,true);
    }

    @Override
    public void remove(int x, int y, int z) {

        String key = getKey(x, y, z);
        cache.invalidate(key);

        ctx.delete(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .execute();
    }

    @Override
    public void run() {

        try {
            while (true) {
                //TODO: batch insert
                TilesRecord record = tileInsertQueue.take();

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

        } catch (InterruptedException e) {
            logger.error("run", e);

        } catch (RuntimeException e){
            logger.error("run", e);

            //re-schedule
            executor.schedule(this, 500, TimeUnit.MILLISECONDS);
        }
    }
}
