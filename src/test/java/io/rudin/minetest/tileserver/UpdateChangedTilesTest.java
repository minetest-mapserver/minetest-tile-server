package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.base.TileServerTest;
import io.rudin.minetest.tileserver.blockdb.tables.Blocks;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.job.UpdateChangedTilesJob;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.service.BlocksRecordService;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.util.CoordinateResolver;
import org.jooq.DSLContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

public class UpdateChangedTilesTest extends TileServerTest {


    @Inject UpdateChangedTilesJob changedTilesJob;
    @Inject TileCache tileCache;
    @Inject @MapDB DSLContext ctx;

    private static final Logger logger = LoggerFactory.getLogger(TileServerTest.class);

    @Test
    public void test() throws IOException {

        final int mapblockX = 0;
        final int mapblockZ = 0;

        CoordinateResolver.TileInfo coordinates = CoordinateResolver.fromCoordinates(mapblockX, mapblockZ);

        logger.debug("Mapblock X={} Z={} / Tile X={} Y={} Zoom={} Width={} Height={}",
                mapblockX, mapblockZ,
                coordinates.x, coordinates.y, coordinates.zoom, coordinates.width, coordinates.height);


        byte[] tile = tileCache.get(0, coordinates.x, coordinates.y, 13);
        Assert.assertNull(tile);

        logger.debug("First result: {}", changedTilesJob.updateChangedTiles());

        tile = tileCache.get(0, coordinates.x, coordinates.y, 13);
        Assert.assertNotNull(tile);

        BlocksRecord block = ctx.selectFrom(BLOCKS)
                .where(BLOCKS.POSX.eq(mapblockX).and(BLOCKS.POSY.eq(0).and(BLOCKS.POSZ.eq(mapblockZ))))
                .fetchOne();

        block.setMtime(System.currentTimeMillis());
        block.update();

        logger.debug("Second result: {}", changedTilesJob.updateChangedTiles());

        tile = tileCache.get(0, coordinates.x, coordinates.y, 13);
        Assert.assertNotNull(tile);


    }
}
