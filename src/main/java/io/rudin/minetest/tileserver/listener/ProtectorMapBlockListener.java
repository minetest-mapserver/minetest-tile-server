package io.rudin.minetest.tileserver.listener;

import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.records.ProtectorRecord;
import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

import static io.rudin.minetest.tileserver.blockdb.tables.Protector.PROTECTOR;

@Singleton
public class ProtectorMapBlockListener {

    private static final String BLOCK_NAME1 = "protector:protect";
    private static final String BLOCK_NAME2 = "protector:protect2";

    @Inject
    public ProtectorMapBlockListener(EventBus eventBus, DSLContext ctx){
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

        ProtectorRecord record = ctx.newRecord(PROTECTOR);
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

        //Clear mapblock data
        ctx
                .deleteFrom(PROTECTOR)
                .where(PROTECTOR.POSX.eq(mapblock.x))
                .and(PROTECTOR.POSY.eq(mapblock.y))
                .and(PROTECTOR.POSZ.eq(mapblock.z))
                .execute();

        if (mapblock.mapping.containsValue(BLOCK_NAME1) || mapblock.mapping.containsValue(BLOCK_NAME2)){

            for (int x=0; x<16; x++){
                for (int y=0; y<16; y++){
                    for (int z=0; z<16; z++){
                        Optional<String> node = mapblock.getNode(x, y, z);

                        if (node.isPresent()){
                            String nodeName = node.get();

                            if (nodeName.equals(BLOCK_NAME1) || nodeName.equals(BLOCK_NAME2)){
                                register(mapblock, x,y,z);
                            }
                        }
                    }
                }
            }

        }

    }

}
