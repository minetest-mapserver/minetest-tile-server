package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.base.TileServerTest;
import io.rudin.minetest.tileserver.blockdb.tables.Blocks;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.job.UpdateChangedTilesJob;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.service.BlocksRecordService;
import io.rudin.minetest.tileserver.service.TileCache;
import org.jooq.DSLContext;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

public class UpdateChangedTilesTest extends TileServerTest {


    @Inject UpdateChangedTilesJob changedTilesJob;
    @Inject TileCache tileCache;
    @Inject @MapDB DSLContext ctx;


    @Test
    public void test() throws IOException {

        byte[] tile = tileCache.get(0, 0, 1, 13);
        Assert.assertNull(tile);

        changedTilesJob.run();

        tile = tileCache.get(0, 0, 1, 13);
        Assert.assertNotNull(tile);

        BlocksRecord block = ctx.selectFrom(BLOCKS)
                .where(BLOCKS.POSX.eq(0).and(BLOCKS.POSY.eq(0).and(BLOCKS.POSZ.eq(0))))
                .fetchOne();

        block.setMtime(System.currentTimeMillis());
        block.update();

        changedTilesJob.run();

        tile = tileCache.get(0, 0, 1, 13);
        Assert.assertNotNull(tile);


    }
}
