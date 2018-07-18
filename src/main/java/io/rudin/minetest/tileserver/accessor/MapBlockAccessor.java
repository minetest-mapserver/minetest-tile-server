package io.rudin.minetest.tileserver.accessor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.MapBlockParser;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.service.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Singleton
public class MapBlockAccessor extends CacheLoader<Coordinate, Optional<MapBlock>> {

    @Inject
    public MapBlockAccessor(BlocksRecordAccessor recordAccessor, EventBus eventBus){
        this.recordAccessor = recordAccessor;
        this.eventBus = eventBus;

        //TODO: disk based cache, ehcache
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(this);
    }

    private final EventBus eventBus;

    private final LoadingCache<Coordinate, Optional<MapBlock>> cache;

    private final BlocksRecordAccessor recordAccessor;

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
        List<BlocksRecord> blocks = recordAccessor.getTopyDownYStride(x, z, minY, maxY);
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
        Optional<BlocksRecord> optionalRecord = recordAccessor.load(coordinate);

        if (optionalRecord.isPresent()) {

            MapBlock mapBlock = MapBlockParser.parse(optionalRecord.get());

            EventBus.MapBlockParsedEvent event = new EventBus.MapBlockParsedEvent();
            event.mapblock = mapBlock;
            eventBus.post(event);

            return Optional.of(mapBlock);

        } else {
            return Optional.empty();
        }
    }
}
