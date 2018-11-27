package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.base.TileServerTest;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.service.BlocksRecordService;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

public class BlocksRecordServiceTest extends TileServerTest {

    @Inject BlocksRecordService blocksRecordService;

    @Test
    public void test(){

        List<BlocksRecord> stride = blocksRecordService.getTopDownYStride(0, 0, -1, 16);

        for (BlocksRecord record: stride){
            System.out.println(new Coordinate(record) + ":" + record.getData().length);
        }

    }
}
