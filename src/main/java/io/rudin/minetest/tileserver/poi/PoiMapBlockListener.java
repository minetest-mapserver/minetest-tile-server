package io.rudin.minetest.tileserver.poi;

import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.accessor.BlocksRecordAccessor;
import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.blockdb.tables.Poi;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.tileserver.blockdb.tables.records.PoiRecord;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.parser.MetadataParser;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.rudin.minetest.tileserver.blockdb.tables.Poi.POI;

@Singleton
public class PoiMapBlockListener {

    private static final String POIBLOCK_NAME = "tileserver:poi";

    @Inject
    public PoiMapBlockListener(EventBus eventBus, DSLContext ctx, TileServerConfig cfg, BlocksRecordAccessor recordAccessor){
        this.eventBus = eventBus;
        this.ctx = ctx;
        this.cfg = cfg;
        this.recordAccessor = recordAccessor;
    }

    private final BlocksRecordAccessor recordAccessor;

    private final TileServerConfig cfg;

    private final DSLContext ctx;

    private final EventBus eventBus;

    public void setup(){
        eventBus.register(this);
    }

    private void registerPOI(MapBlock mapBlock, int x, int y, int z){
        Metadata metadata;

        try {
            metadata = MetadataParser.parse(mapBlock.metadata, mapBlock.metadataLength);
        } catch (Exception e){

            if (cfg.dumpInvalidMapblockData()){
                try (OutputStream output = new FileOutputStream("mapblock_" + mapBlock.x + "." + mapBlock.y + "." + mapBlock.z + ".raw")) {

                    Optional<BlocksRecord> record = recordAccessor.get(new Coordinate(mapBlock.x, mapBlock.y, mapBlock.z));
                    output.write(record.get().getData());

                } catch (Exception e2){
                    e2.printStackTrace();
                }
            }

            throw new IllegalArgumentException("parse-error on mapblock: " + mapBlock.x + "/" + mapBlock.y + "/" + mapBlock.z, e);

        }

        int position = MapBlock.toPosition(x, y, z);

        Map<String, String> map = metadata.map.get(position);

        int posx = (mapBlock.x * 16) + x;
        int posy = (mapBlock.y * 16) + y;
        int posz = (mapBlock.z * 16) + z;

        //TODO: replace new values
        //TODO: remove old poi

        PoiRecord poiRecord = ctx.newRecord(POI);
        poiRecord.setCategory(map.get("category"));
        poiRecord.setName(map.get("name"));
        poiRecord.setOwner(map.get("owner"));
        poiRecord.setActive(map.get("active").equals("1"));
        poiRecord.setMtime(System.currentTimeMillis());

        poiRecord.setPosx(mapBlock.x);
        poiRecord.setPosy(mapBlock.y);
        poiRecord.setPosz(mapBlock.z);

        poiRecord.setX(posx);
        poiRecord.setY(posy);
        poiRecord.setZ(posz);

        poiRecord.insert();

    }

    @Subscribe
    public void onMapBlock(EventBus.MapBlockParsedEvent event){

        MapBlock mapblock = event.mapblock;

        if (mapblock.mapping.containsValue(POIBLOCK_NAME)){

            //Clear mapblock poi's
            ctx
                    .deleteFrom(POI)
                    .where(POI.POSX.eq(mapblock.x))
                    .and(POI.POSY.eq(mapblock.y))
                    .and(POI.POSZ.eq(mapblock.z))
                    .execute();



            for (int x=0; x<16; x++){
                for (int y=0; y<16; y++){
                    for (int z=0; z<16; z++){
                        Optional<String> node = mapblock.getNode(x, y, z);

                        if (node.isPresent()){
                            String nodeName = node.get();

                            if (nodeName.equals(POIBLOCK_NAME)){
                                registerPOI(mapblock, x,y,z);
                            }
                        }
                    }
                }
            }

        }

    }

}
