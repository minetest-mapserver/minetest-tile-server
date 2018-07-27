package io.rudin.minetest.tileserver.listener;

import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.records.MissionsRecord;
import io.rudin.minetest.tileserver.blockdb.tables.records.TravelnetRecord;
import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

import static io.rudin.minetest.tileserver.blockdb.tables.Missions.MISSIONS;

@Singleton
public class MissionBlockListener {

    private static final String BLOCK_NAME = "missions:mission";

    @Inject
    public MissionBlockListener(EventBus eventBus, DSLContext ctx){
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

        int posx = (mapBlock.x * 16) + x;
        int posy = (mapBlock.y * 16) + y;
        int posz = (mapBlock.z * 16) + z;

        MissionsRecord record = ctx.newRecord(MISSIONS);
        record.setName(map.get("name"));
        record.setDescription(map.get("description"));
        record.setTime(Integer.parseInt(map.get("time")));
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
                .deleteFrom(MISSIONS)
                .where(MISSIONS.POSX.eq(mapblock.x))
                .and(MISSIONS.POSY.eq(mapblock.y))
                .and(MISSIONS.POSZ.eq(mapblock.z))
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
