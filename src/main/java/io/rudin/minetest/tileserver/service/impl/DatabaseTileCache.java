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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static io.rudin.minetest.tileserver.tiledb.tables.Tiles.TILES;

@Singleton
public class DatabaseTileCache implements TileCache {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTileCache.class);

    @Inject
    public DatabaseTileCache(@TileDB DSLContext ctx, ExecutorService executor){
        this.ctx = ctx;
        this.lock = Striped.lazyWeakReadWriteLock(50);
        this.executor = executor;

        this.cache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .weakValues()
                .maximumSize(500)
                .build();
    }


    private final Cache<String, byte[]> cache;

    private final ExecutorService executor;

    private final Striped<ReadWriteLock> lock;

    private final DSLContext ctx;

    private String getKey(int x, int y, int z){
        return x + "/" + y + "/" + z;
    }

    private ReadWriteLock getLock(int x, int y, int z){
        return lock.get(getKey(x,y,z));
    }

    @Override
    public void put(int x, int y, int z, byte[] data) {

        final String key = getKey(x,y,z);

        cache.put(key, data);

        ReadWriteLock lock = getLock(x, y, z);

        long now = System.currentTimeMillis();

        TilesRecord record = ctx.newRecord(TILES);
        record.setTile(data);
        record.setX(x);
        record.setY(y);
        record.setZ(z);
        record.setMtime(now);

        AsyncTileInsertJob job = new AsyncTileInsertJob(record, lock);
        executor.submit(job);
    }



    /**
     * Async insert job
     */
    public class AsyncTileInsertJob implements Runnable {

        AsyncTileInsertJob(TilesRecord record, ReadWriteLock lock){
            this.record = record;
            this.lock = lock;
        }

        private final ReadWriteLock lock;

        private final TilesRecord record;

        @Override
        public void run() {
            Lock writeLock = lock.writeLock();
            writeLock.lock();

            long now = System.currentTimeMillis();

            try {
                //re-check in critical section
                if (has(record.getX(),record.getY(),record.getZ(), false)){
                    //already inserted
                    return;
                }


                record.insert();

            } finally {
                writeLock.unlock();

                long diff = System.currentTimeMillis() - now;
                if (diff > 1000){
                    logger.warn("Insert of tile {}/{}/{} took {} ms", record.getX(), record.getY(), record.getZ(), diff);
                }
            }
        }
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

        ReadWriteLock lock = getLock(x, y, z);
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            ctx.delete(TILES)
                    .where(TILES.X.eq(x))
                    .and(TILES.Y.eq(y))
                    .and(TILES.Z.eq(z))
                    .execute();

        } finally {
            writeLock.unlock();

        }
    }
}
