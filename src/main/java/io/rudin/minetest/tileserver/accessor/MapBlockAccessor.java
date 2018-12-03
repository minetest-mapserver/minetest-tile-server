package io.rudin.minetest.tileserver.accessor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.util.MapBlock;
import io.rudin.minetest.tileserver.util.MapBlockParser;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.service.BlocksRecordService;
import io.rudin.minetest.tileserver.service.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Singleton
public class MapBlockAccessor extends CacheLoader<Coordinate, Optional<MapBlock>> {

    private static final Logger logger = LoggerFactory.getLogger(MapBlockAccessor.class);

    @Inject
    public MapBlockAccessor(BlocksRecordService blocksRecordService, EventBus eventBus, TileServerConfig cfg){
        this.blocksRecordService = blocksRecordService;
        this.eventBus = eventBus;

        //TODO: disk based cache, ehcache
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(500)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(this);

        this.cfg = cfg;
    }

    private final TileServerConfig cfg;

    private final EventBus eventBus;

    private final LoadingCache<Coordinate, Optional<MapBlock>> cache;

    private final BlocksRecordService blocksRecordService;

    public Optional<MapBlock> get(Coordinate coords){
        try {
            return cache.get(coords);
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("get", e);
        }
    }

    public void prefetchTopDownYStride(int x, int z, int minY, int maxY){

        for (int y=minY; y<=maxY; y++){
            Optional<MapBlock> optional = cache.getIfPresent(new Coordinate(x, y, z));
            if (optional != null && optional.isPresent())
                return;
        }

        //do prefetch
        List<BlocksRecord> blocks = blocksRecordService.getTopDownYStride(x, z, minY, maxY);
        for (BlocksRecord record: blocks){
            MapBlock mapBlock = MapBlockParser.parse(record);

            EventBus.MapBlockParsedEvent event = new EventBus.MapBlockParsedEvent();
            event.mapblock = mapBlock;
            eventBus.post(event);

            update(mapBlock);
        }
    }

    public void invalidate(Coordinate coordinate){
        cache.invalidate(coordinate);
    }

    public void update(MapBlock mapBlock){
        cache.put(new Coordinate(mapBlock), Optional.of(mapBlock));
    }

    @Override
    public Optional<MapBlock> load(Coordinate coordinate) throws Exception {
        Optional<BlocksRecord> optionalRecord = blocksRecordService.get(coordinate);

        if (optionalRecord.isPresent()) {

            BlocksRecord record = optionalRecord.get();

            try {
                MapBlock mapBlock = MapBlockParser.parse(record);

                EventBus.MapBlockParsedEvent event = new EventBus.MapBlockParsedEvent();
                event.mapblock = mapBlock;
                eventBus.post(event);

                return Optional.of(mapBlock);

            } catch (Exception e){
                logger.error("load", e);

                if (cfg.dumpFailedMapblocks()){
                    //dump mapblock and stacktrace

                    File dumpDir = new File("mapblocks");
                    if (!dumpDir.isDirectory())
                        dumpDir.mkdir();

                    String nameBase = record.getPosx() + "_" + record.getPosy() + "_" + record.getPosz();
                    File dumpFile = new File(dumpDir, nameBase + ".mapblock");
                    File errorFile = new File(dumpDir, nameBase + ".error");

                    try (OutputStream output = new FileOutputStream(dumpFile)){
                        output.write(record.getData());
                    }

                    try (OutputStream output = new FileOutputStream(errorFile)){
                        e.printStackTrace(new PrintWriter(errorFile));
                    }
                }

                throw e;

            }

        } else {
            return Optional.empty();
        }
    }
}
