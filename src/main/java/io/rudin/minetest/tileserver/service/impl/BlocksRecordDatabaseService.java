package io.rudin.minetest.tileserver.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.service.BlocksRecordService;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

@Singleton
public class BlocksRecordDatabaseService
        extends CacheLoader<Coordinate, Optional<BlocksRecord>>
        implements BlocksRecordService {


    private static final Logger logger = LoggerFactory.getLogger(BlocksRecordDatabaseService.class);

    @Inject
    public BlocksRecordDatabaseService(@MapDB DSLContext ctx, TileServerConfig cfg){
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


    @Override
    public Optional<BlocksRecord> get(Coordinate coords){
        try {
            return cache.get(coords);
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("get", e);
        }
    }

    @Override
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

    public static File getLocalMapBlockFile(Coordinate coordinate){
        File mapblockDir = new File("mapblocks");
        if (!mapblockDir.isDirectory())
            mapblockDir.mkdir();

        return new File(mapblockDir, coordinate.x + "." + coordinate.y + "." + coordinate.z);
    }

    private void saveMapBlockLocally(Coordinate coordinate, BlocksRecord block){
        if (!cfg.saveMapBlocks() || block == null)
            return;

        File file = getLocalMapBlockFile(coordinate);
        try (OutputStream output = new FileOutputStream(file)){
            output.write(block.getData());

        } catch (Exception e){
            //debug option, nobody cares...
            e.printStackTrace();
        }

    }

    @Override
    public void update(BlocksRecord block){
        Coordinate coordinate = new Coordinate(block);
        cache.put(coordinate, Optional.of(block));
        saveMapBlockLocally(coordinate, block);
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

        if (record.isPresent()){
            saveMapBlockLocally(coordinate, record.get());
        }

        return record;
    }

}
