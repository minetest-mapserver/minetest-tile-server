package io.rudin.minetest.tileserver.poi;

import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.Poi;
import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.parser.MetadataParser;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class PoiMapBlockListener {

    private static final String POIBLOCK_NAME = "tileserver:poi";

    @Inject
    public PoiMapBlockListener(EventBus eventBus, DSLContext ctx){
        this.eventBus = eventBus;
        this.ctx = ctx;
    }

    private final DSLContext ctx;

    private final EventBus eventBus;

    public void setup(){
        eventBus.register(this);
    }

    private void registerPOI(MapBlock mapBlock, int x, int y, int z){
        Metadata metadata = MetadataParser.parse(mapBlock.metadata, mapBlock.metadataLength);

        int position = MapBlock.toPosition(x, y, z);

        Map<String, String> map = metadata.map.get(position);

        System.out.println(map);//XXX
        //TODO: db stuff

    }

    @Subscribe
    public void onMapBlock(EventBus.MapBlockParsedEvent event){

        MapBlock mapblock = event.mapblock;

        if (mapblock.mapping.containsValue(POIBLOCK_NAME)){

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
