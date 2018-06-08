package io.rudin.minetest.tileserver.util;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

/**
 * Cached mapblock accessor/fetcher
 */
@Singleton
public class MapBlockAccessor extends CacheLoader<MapBlockAccessor.Key, List<BlocksRecord>> {

    private static final Logger logger = LoggerFactory.getLogger(MapBlockAccessor.class);

    @Inject
    public MapBlockAccessor(DSLContext ctx, TileServerConfig cfg){
        this.ctx = ctx;
        this.maxY = cfg.tilesMaxY();
        this.minY = cfg.tilesMinY();

        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .maximumSize(500)
                .build(this);
    }

    private final LoadingCache<Key, List<BlocksRecord>> cache;

    @Override
    public List<BlocksRecord> load(Key key) throws Exception {


        long now = System.currentTimeMillis();

        Result<BlocksRecord> blocks = ctx
                .selectFrom(BLOCKS)
                .where(
                        BLOCKS.POSX.eq(key.x)
                                .and(BLOCKS.POSZ.eq(key.z))
                                .and(BLOCKS.POSY.ge(this.minY))
                                .and(BLOCKS.POSY.le(this.maxY))
                )
                .orderBy(BLOCKS.POSY.desc()) // top-first
                .fetch();

        long fetchTime = System.currentTimeMillis() - now;

        logger.debug("Got {} blocks for mapblockX={} mapblockZ={}",
                blocks.size(),
                key.x,
                key.z
        );

        if (fetchTime > 500){
            logger.warn("Mapblock fetch of x={} z={} took {} ms", key.x, key.z, fetchTime);
        }


        return blocks;
    }

    /**
     * Key class for 2d coordinate
     */
    public static class Key {
        Key(int x, int z){
            this.x = x;
            this.z = z;
        }

        final int x, z;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return x == key.x &&
                    z == key.z;
        }

        @Override
        public int hashCode() {

            return Objects.hash(x, z);
        }
    }

    private final int maxY, minY;

    private final DSLContext ctx;

    public List<BlocksRecord> getTopDownYStride(int x, int z) throws ExecutionException {
        return cache.get(new Key(x,z));
    }

    /**
     * Returns the mapblock at given mapblock coordinates, or null if none there
     * @param x
     * @param y
     * @param z
     * @return
     * @throws DataFormatException
     */
    public BlocksRecord get(int x, int y, int z) throws DataFormatException {
        List<BlocksRecord> blocks = cache.getIfPresent(new Key(x, z));
        for (BlocksRecord r: blocks){
            if (r.getPosy() == y)
                return r;
        }

        return null;
    }

}
