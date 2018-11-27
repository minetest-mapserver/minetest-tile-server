package io.rudin.minetest.tileserver.service;

import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

import java.util.List;
import java.util.Optional;

public interface BlocksRecordService {

    List<BlocksRecord> getTopDownYStride(int x, int z, int minY, int maxY);

    Optional<BlocksRecord> get(Coordinate coords);

    void update(BlocksRecord block);
}
