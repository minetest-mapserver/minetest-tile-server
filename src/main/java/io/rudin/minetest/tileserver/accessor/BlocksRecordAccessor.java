package io.rudin.minetest.tileserver.accessor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

@Singleton
public class BlocksRecordAccessor extends CacheLoader<Coordinate, Optional<BlocksRecord>> {

    private static final Logger logger = LoggerFactory.getLogger(BlocksRecordAccessor.class);

    @Inject
    public BlocksRecordAccessor(@MapDB DSLContext ctx, TileServerConfig cfg){
        this.ctx = ctx;
        this.cfg = cfg;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(this);
    }

    private final TileServerConfig cfg;

    private final LoadingCache<Coordinate, Optional<BlocksRecord>> cache;

    private final DSLContext ctx;

    public Optional<BlocksRecord> get(Coordinate coords){
        try {
            return cache.get(coords);
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("get", e);
        }
    }

    public List<BlocksRecord> getTopDownYStride(int x, int z, int minY, int maxY) {

        long start = System.currentTimeMillis();

        List<BlocksRecord> list = ctx
                .selectFrom(BLOCKS)
                .where(BLOCKS.POSX.eq(x))
                .and(BLOCKS.POSY.ge(minY))
                .and(BLOCKS.POSY.le(maxY))
                .and(BLOCKS.POSZ.eq(z))
                .orderBy(BLOCKS.POSY.desc())
                .fetch();

        long diff = System.currentTimeMillis() - start;

        if (diff > 500 && cfg.logQueryPerformance()){
            logger.warn("getTopDownYStride took {} ms", diff);
        }

        for (BlocksRecord record: list){
            update(record);
        }

        return list;
    }


    public void update(BlocksRecord block){
        cache.put(new Coordinate(block), Optional.of(block));
    }

    @Override
    public Optional<BlocksRecord> load(Coordinate coordinate) throws Exception {

        long start = System.currentTimeMillis();


        Optional<BlocksRecord> record = ctx
                .selectFrom(BLOCKS)
                .where(BLOCKS.POSX.eq(coordinate.x))
                .and(BLOCKS.POSY.eq(coordinate.y))
                .and(BLOCKS.POSZ.eq(coordinate.z))
                .fetchOptional();


        long diff = System.currentTimeMillis() - start;

        if (diff > 500 && cfg.logQueryPerformance()){
            logger.warn("load took {} ms", diff);
        }
        return record;
    }
}
