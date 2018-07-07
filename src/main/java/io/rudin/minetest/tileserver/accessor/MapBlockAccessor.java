package io.rudin.minetest.tileserver.accessor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.MapBlockParser;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;

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
    public MapBlockAccessor(BlocksRecordAccessor recordAccessor){
        this.recordAccessor = recordAccessor;

        this.cache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build(this);
    }

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

        if (optionalRecord.isPresent())
            return Optional.of(MapBlockParser.parse(optionalRecord.get()));
        else
            return Optional.empty();
    }
}
