package io.rudin.minetest.tileserver.service;

import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.service.impl.BlocksRecordDatabaseService;
import io.rudin.minetest.tileserver.util.StreamUtil;
import sun.security.action.OpenFileInputStreamAction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class BlocksRecordLocalService implements BlocksRecordService {

    private final Map<Coordinate, BlocksRecord> cache = new HashMap<>();

    @Override
    public List<BlocksRecord> getTopDownYStride(int x, int z, int minY, int maxY) {

        List<BlocksRecord> result = new ArrayList<>();

        for (int y=minY; y<=maxY; y++){
            Coordinate coordinate = new Coordinate(x, y, z);

            Optional<BlocksRecord> optionalRecord = get(coordinate);
            if (optionalRecord.isPresent())
                result.add(optionalRecord.get());
        }

        result.sort(Comparator.comparingInt(BlocksRecord::getPosy));

        return result;
    }

    public static File getLocalMapBlockFile(Coordinate coordinate){
        File mapblockDir = new File("testdata/mapblocks");
        if (!mapblockDir.isDirectory())
            mapblockDir.mkdir();

        return new File(mapblockDir, coordinate.x + "." + coordinate.y + "." + coordinate.z);
    }

    @Override
    public Optional<BlocksRecord> get(Coordinate coords) {
        BlocksRecord record = cache.get(coords);

        if (record != null)
            return Optional.of(record);

        File file = getLocalMapBlockFile(coords);

        if (!file.isFile())
            return Optional.empty();

        try (InputStream input = new FileInputStream(file)){
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            StreamUtil.copyStream(input, output);

            record = new BlocksRecord();
            record.setData(output.toByteArray());
            record.setMtime(file.lastModified());
            record.setPosx(coords.x);
            record.setPosy(coords.y);
            record.setPosz(coords.z);
            cache.put(coords, record);
            return Optional.of(record);

        } catch (Exception e){
            throw new IllegalArgumentException(e);
        }

    }

    @Override
    public void update(BlocksRecord block) {
        cache.put(new Coordinate(block), block);
    }
}
