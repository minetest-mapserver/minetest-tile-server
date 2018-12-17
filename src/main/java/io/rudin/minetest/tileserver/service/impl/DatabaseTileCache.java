package io.rudin.minetest.tileserver.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.TileDB;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.tiledb.tables.records.TilesRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.*;

import static io.rudin.minetest.tileserver.tiledb.tables.Tiles.TILES;

@Singleton
public class DatabaseTileCache implements TileCache {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTileCache.class);

    @Inject
    public DatabaseTileCache(@TileDB DSLContext ctx, ScheduledExecutorService executor, TileServerConfig cfg){
        this.ctx = ctx;
        this.executor = executor;
        this.cfg = cfg;

    }

    private final TileServerConfig cfg;

    private final ScheduledExecutorService executor;

    private final DSLContext ctx;

    @Override
    public void put(int layerId, int x, int y, int z, byte[] data) {

        TilesRecord record = ctx.newRecord(TILES);
        record.setLayerid(layerId);
        record.setX(x);
        record.setY(y);
        record.setZ(z);
        record.setTile(data);
        record.setMtime(System.currentTimeMillis());

        insertOrupdate(record);
    }



    @Override
    public byte[] get(int layerId, int x, int y, int z) {

        return ctx
                .select(TILES.TILE)
                .from(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .and(TILES.LAYERID.eq(layerId))
                .fetchOne(TILES.TILE);

    }


    @Override
    public boolean has(int layerId, int x, int y, int z) {

        Integer count = ctx
                .select(DSL.count())
                .from(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .and(TILES.LAYERID.eq(layerId))
                .fetchOne(DSL.count());

        return count > 0;
    }



    @Override
    public void remove(int layerId, int x, int y, int z) {


        long start = System.currentTimeMillis();

        ctx.delete(TILES)
                .where(TILES.X.eq(x))
                .and(TILES.Y.eq(y))
                .and(TILES.Z.eq(z))
                .and(TILES.LAYERID.eq(layerId))
                .execute();

        long diff = System.currentTimeMillis() - start;
        if (diff > 500){
            logger.warn("Tile removal ({},{},{}) took {} ms", x,y,z, diff);
        }
    }

    @Override
    public long getLatestTimestamp() {
        Long mtime = ctx
                .select(DSL.max(TILES.MTIME))
                .from(TILES)
                .fetchOne(DSL.max(TILES.MTIME));

        if (mtime == null)
            return 0L;
        else
            return mtime;
    }

    @Override
    public void close() {

    }

    private void insertOrupdate(TilesRecord record){
        if (ctx.dialect() == SQLDialect.POSTGRES){
            //upsert
            ctx
                    .insertInto(TILES, record.fields())
                    .values(record.intoArray())
                    .onDuplicateKeyUpdate()
                    .set(TILES.X, record.getX())
                    .set(TILES.Y, record.getY())
                    .set(TILES.Z, record.getZ())
                    .set(TILES.LAYERID, record.getLayerid())
                    .set(TILES.MTIME, record.getMtime())
                    .set(TILES.TILE, record.getTile())
                    .execute();

        } else {
            //fallback
            TilesRecord existingRecord = ctx.selectFrom(TILES)
                    .where(TILES.X.eq(record.getX()))
                    .and(TILES.Y.eq(record.getY()))
                    .and(TILES.Z.eq(record.getZ()))
                    .and(TILES.LAYERID.eq(record.getLayerid()))
                    .fetchOne();

            if (existingRecord != null){
                //update
                existingRecord.setTile(record.getTile());
                existingRecord.setMtime(record.getMtime());
                existingRecord.update();

            } else {
                //insert
                record.insert();

            }

        }

    }

}
