package io.rudin.minetest.tileserver.util;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.MapBlockParser;
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
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

/**
 * Cached mapblock accessor/fetcher
 */
@Singleton
public class MapBlockAccessor extends CacheLoader<MapBlockAccessor.Key, List<MapBlock>> {

    private static final Logger logger = LoggerFactory.getLogger(MapBlockAccessor.class);

    @Inject
    public MapBlockAccessor(DSLContext ctx, TileServerConfig cfg){
        this.ctx = ctx;
        this.maxY = cfg.tilesMaxY();
        this.minY = cfg.tilesMinY();

        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(20, TimeUnit.SECONDS)
                .maximumSize(500)
                .build(this);
    }

    //TODO: hashmap instead of list for y values
    private final LoadingCache<Key, List<MapBlock>> cache;

    @Override
    public List<MapBlock> load(Key key) throws Exception {


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

        logger.debug("Got {} blocks for mapblockX={} mapblockZ={} in {} ms",
                blocks.size(),
                key.x,
                key.z,
                fetchTime
        );

        if (fetchTime > 500){
            logger.warn("Mapblock fetch of x={} z={} took {} ms", key.x, key.z, fetchTime);
        }


        return blocks.stream().map(MapBlockParser::parse).collect(Collectors.toList());
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

    public List<MapBlock> getTopDownYStride(int x, int z) throws ExecutionException {
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
    public MapBlock get(int x, int y, int z) throws DataFormatException, ExecutionException {
        List<MapBlock> blocks = cache.get(new Key(x, z));
        for (MapBlock block: blocks){
            if (block.y == y)
                return block;
        }

        return null;
    }

}
