package io.rudin.minetest.tileserver.listener;

import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.records.TrainlineRecord;
import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

import static io.rudin.minetest.tileserver.blockdb.tables.Trainline.TRAINLINE;

@Singleton
public class TrainBlockListener {

    private static final String BLOCK_NAME = "tileserver:train";

    @Inject
    public TrainBlockListener(EventBus eventBus, DSLContext ctx){
        this.eventBus = eventBus;
        this.ctx = ctx;
    }

    private final DSLContext ctx;

    private final EventBus eventBus;

    public void setup(){
        eventBus.register(this);
    }

    private void register(MapBlock mapBlock, int x, int y, int z){
        Metadata metadata = mapBlock.getMetadata();

        int position = MapBlock.toPosition(x, y, z);

        Map<String, String> map = metadata.map.get(position);

        String active = map.get("active");

        if (active == null || !active.equals("1"))
                return;

        int posx = (mapBlock.x * 16) + x;
        int posy = (mapBlock.y * 16) + y;
        int posz = (mapBlock.z * 16) + z;

        String station = map.get("station");
        String line = map.get("line");

        if (line == null)
            //No trainline without line info
            return;

        TrainlineRecord record = ctx.newRecord(TRAINLINE);
        record.setStation(station == null ? "" : station);
        record.setLine(line);
        record.setIndex(Integer.parseInt(map.get("index")));
        record.setOwner(map.get("owner"));
        record.setMtime(System.currentTimeMillis());

        record.setPosx(mapBlock.x);
        record.setPosy(mapBlock.y);
        record.setPosz(mapBlock.z);

        record.setX(posx);
        record.setY(posy);
        record.setZ(posz);

        record.insert();
    }

    @Subscribe
    public void onMapBlock(EventBus.MapBlockParsedEvent event){

        MapBlock mapblock = event.mapblock;

        //Clear entries for this mapblock
        ctx
                .deleteFrom(TRAINLINE)
                .where(TRAINLINE.POSX.eq(mapblock.x))
                .and(TRAINLINE.POSY.eq(mapblock.y))
                .and(TRAINLINE.POSZ.eq(mapblock.z))
                .execute();

        if (mapblock.mapping.containsValue(BLOCK_NAME)){

            for (int x=0; x<16; x++){
                for (int y=0; y<16; y++){
                    for (int z=0; z<16; z++){
                        Optional<String> node = mapblock.getNode(x, y, z);

                        if (node.isPresent() && node.get().equals(BLOCK_NAME)){
                                register(mapblock, x,y,z);
                        }
                    }
                }
            }

        }

    }

}
