package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.base.TileServerTest;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.job.UpdateChangedTilesJob;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.util.coordinate.CoordinateFactory;
import io.rudin.minetest.tileserver.util.coordinate.MapBlockCoordinate;
import io.rudin.minetest.tileserver.util.coordinate.TileCoordinate;
import org.jooq.DSLContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

public class UpdateChangedTilesTest extends TileServerTest {


    @Inject UpdateChangedTilesJob changedTilesJob;
    @Inject TileCache tileCache;
    @Inject @MapDB DSLContext ctx;

    private static final Logger logger = LoggerFactory.getLogger(TileServerTest.class);

    @Test
    public void test() throws IOException {

        uploadMapBlocksToDatabase("testdata/mapblocks");

        MapBlockCoordinate mapBlockCoordinate = new MapBlockCoordinate(0, 0);
        TileCoordinate tileCoordinate = CoordinateFactory.getTileCoordinateFromMapBlock(mapBlockCoordinate);
        tileCoordinate = CoordinateFactory.getZoomedOutTile(tileCoordinate);

        logger.debug("Mapblock X={} Z={} / Tile X={} Y={} Zoom={}",
                mapBlockCoordinate.x, mapBlockCoordinate.z,
                tileCoordinate.x, tileCoordinate.y, tileCoordinate.zoom);


        byte[] tile = tileCache.get(0, tileCoordinate.x, tileCoordinate.y, tileCoordinate.zoom);
        Assert.assertNull(tile);

        logger.debug("First result (init): {}", changedTilesJob.updateChangedTiles());

        tile = tileCache.get(0, tileCoordinate.x, tileCoordinate.y, tileCoordinate.zoom);
        Assert.assertNotNull(tile);

        BlocksRecord block = ctx.selectFrom(BLOCKS)
                .where(BLOCKS.POSX.eq(mapBlockCoordinate.x).and(BLOCKS.POSY.eq(0).and(BLOCKS.POSZ.eq(mapBlockCoordinate.z))))
                .fetchOne();

        block.setMtime(System.currentTimeMillis());
        block.update();

        logger.debug("Second result (changed): {}", changedTilesJob.updateChangedTiles());

        tile = tileCache.get(0, tileCoordinate.x, tileCoordinate.y, tileCoordinate.zoom);
        Assert.assertNotNull(tile);

        logger.debug("Third result (unchanged): {}", changedTilesJob.updateChangedTiles());

        tile = tileCache.get(0, tileCoordinate.x, tileCoordinate.y, tileCoordinate.zoom);
        Assert.assertNotNull(tile);


    }
}
