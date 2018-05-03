package io.rudin.minetest.tileserver.service.impl;

import com.google.common.util.concurrent.Striped;
import io.rudin.minetest.tileserver.blockdb.tables.records.TileserverTilesRecord;
import io.rudin.minetest.tileserver.service.TileCache;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import static io.rudin.minetest.tileserver.blockdb.tables.TileserverTiles.TILESERVER_TILES;

@Singleton
public class DatabaseTileCache implements TileCache {

    @Inject
    public DatabaseTileCache(DSLContext ctx){
        this.ctx = ctx;
        this.lock = Striped.lazyWeakReadWriteLock(50);
    }

    private final Striped<ReadWriteLock> lock;

    private final DSLContext ctx;

    private ReadWriteLock getLock(int x, int y, int z){
        return lock.get(x + "/" + y + "/" + z);
    }

    @Override
    public void put(int x, int y, int z, byte[] data) {
        ReadWriteLock lock = getLock(x, y, z);
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            //re-check in lock
            if (has(x,y,z))
                return;

            TileserverTilesRecord record = ctx.newRecord(TILESERVER_TILES);
            record.setTile(data);
            record.setX(x);
            record.setY(y);
            record.setZ(z);
            record.setMtime(System.currentTimeMillis());
            record.insert();

        } finally {
            writeLock.unlock();

        }
    }

    @Override
    public byte[] get(int x, int y, int z) {
        ReadWriteLock lock = getLock(x, y, z);
        Lock readLock = lock.readLock();
        readLock.lock();

        try {
            return ctx
                    .select(TILESERVER_TILES.TILE)
                    .from(TILESERVER_TILES)
                    .where(TILESERVER_TILES.X.eq(x))
                    .and(TILESERVER_TILES.Y.eq(y))
                    .and(TILESERVER_TILES.Z.eq(z))
                    .fetchOne(TILESERVER_TILES.TILE);

        } finally {
            readLock.unlock();

        }
    }

    @Override
    public boolean has(int x, int y, int z) {
        ReadWriteLock lock = getLock(x, y, z);
        Lock readLock = lock.readLock();
        readLock.lock();

        try {
            Integer count = ctx
                    .select(DSL.count())
                    .from(TILESERVER_TILES)
                    .where(TILESERVER_TILES.X.eq(x))
                    .and(TILESERVER_TILES.Y.eq(y))
                    .and(TILESERVER_TILES.Z.eq(z))
                    .fetchOne(DSL.count());

            return count > 0;

        } finally {
            readLock.unlock();

        }
    }

    @Override
    public void remove(int x, int y, int z) {
        ReadWriteLock lock = getLock(x, y, z);
        Lock writeLock = lock.writeLock();
        writeLock.lock();

        try {
            ctx.delete(TILESERVER_TILES)
                    .where(TILESERVER_TILES.X.eq(x))
                    .and(TILESERVER_TILES.Y.eq(y))
                    .and(TILESERVER_TILES.Z.eq(z))
                    .execute();

        } finally {
            writeLock.unlock();

        }
    }
}
