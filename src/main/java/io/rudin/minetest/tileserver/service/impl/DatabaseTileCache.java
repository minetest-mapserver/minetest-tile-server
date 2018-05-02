package io.rudin.minetest.tileserver.service.impl;

import io.rudin.minetest.tileserver.blockdb.tables.records.TileserverTilesRecord;
import io.rudin.minetest.tileserver.service.TileCache;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static io.rudin.minetest.tileserver.blockdb.tables.TileserverTiles.TILESERVER_TILES;

@Singleton
public class DatabaseTileCache implements TileCache {

    @Inject
    public DatabaseTileCache(DSLContext ctx){
        this.ctx = ctx;
    }

    private final DSLContext ctx;

    @Override
    public void put(int x, int y, int z, byte[] data) throws IOException {
        TileserverTilesRecord record = ctx.newRecord(TILESERVER_TILES);
        record.setTile(data);
        record.setX(x);
        record.setY(y);
        record.setZ(z);
        record.setMtime(System.currentTimeMillis());
        record.insert();
    }

    @Override
    public byte[] get(int x, int y, int z) throws IOException {
        return ctx
                .select(TILESERVER_TILES.TILE)
                .from(TILESERVER_TILES)
                .where(TILESERVER_TILES.X.eq(x))
                .and(TILESERVER_TILES.Y.eq(y))
                .and(TILESERVER_TILES.Z.eq(z))
                .fetchOne(TILESERVER_TILES.TILE);
    }

    @Override
    public boolean has(int x, int y, int z) {
        Integer count = ctx
                .select(DSL.count())
                .from(TILESERVER_TILES)
                .where(TILESERVER_TILES.X.eq(x))
                .and(TILESERVER_TILES.Y.eq(y))
                .and(TILESERVER_TILES.Z.eq(z))
                .fetchOne(DSL.count());

        return count > 0;
    }

    @Override
    public void remove(int x, int y, int z) {
        ctx.delete(TILESERVER_TILES)
            .where(TILESERVER_TILES.X.eq(x))
            .and(TILESERVER_TILES.Y.eq(y))
            .and(TILESERVER_TILES.Z.eq(z))
            .execute();
    }
}
