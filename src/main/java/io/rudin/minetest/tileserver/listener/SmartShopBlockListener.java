package io.rudin.minetest.tileserver.listener;

import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.records.ShopRecord;
import io.rudin.minetest.tileserver.parser.Inventory;
import io.rudin.minetest.tileserver.parser.Item;
import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.service.EventBus;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static io.rudin.minetest.tileserver.blockdb.tables.Shop.SHOP;


public class SmartShopBlockListener {


    private static final String BLOCK_NAME = "smartshop:shop";
    private static final String TYPE = "Smartshop";

    @Inject
    public SmartShopBlockListener(EventBus eventBus, DSLContext ctx){
        this.eventBus = eventBus;
        this.ctx = ctx;
    }

    private final DSLContext ctx;

    private final EventBus eventBus;

    public void setup(){
        eventBus.register(this);
    }

    private void registerShop(MapBlock mapBlock, int x, int y, int z, Inventory give, Inventory pay, Inventory main, String owner){
        String in_item = pay.items.get(0).name;
        int in_count = Math.max(1, pay.items.get(0).count);

        String out_item = give.items.get(0).name;
        int out_count = Math.max(1, give.items.get(0).count);


        int stock = 0;

        for (Item item: main.items){
            if (item.name.equals(out_item)){
                stock += item.count;
            }
        }

        //multiples of out_count
        int stock_factor = (int)Math.floor((double)stock / (double)out_count);

        ShopRecord record = ctx.newRecord(SHOP);
        record.setType(TYPE);
        record.setOwner(owner);
        record.setMtime(System.currentTimeMillis());

        record.setInItem(in_item);
        record.setInCount(in_count);
        record.setOutItem(out_item);
        record.setOutCount(out_count);
        record.setOutStock(stock_factor);
        record.setActive(stock_factor > 0);

        record.setPosx(mapBlock.x);
        record.setPosy(mapBlock.y);
        record.setPosz(mapBlock.z);

        int posx = (mapBlock.x * 16) + x;
        int posy = (mapBlock.y * 16) + y;
        int posz = (mapBlock.z * 16) + z;

        record.setX(posx);
        record.setY(posy);
        record.setZ(posz);

        record.insert();
    }

    private void register(MapBlock mapBlock, int x, int y, int z){
        Metadata metadata = mapBlock.getMetadata();

        int position = MapBlock.toPosition(x, y, z);

        Map<String, Inventory> inv = metadata.inventories.get(position);
        Map<String, String> mdMap = metadata.map.get(position);

        Inventory main = inv.get("main");
        String owner = mdMap.get("owner");

        for (int i=1; i<=4; i++) {
            Inventory pay = inv.get("pay" + i);
            Inventory give = inv.get("give" + i);

            if (!pay.items.isEmpty() && !give.items.isEmpty() && !main.items.isEmpty())
                registerShop(mapBlock, x, y, z, give, pay, main, owner);
        }
    }

    @Subscribe
    public void onMapBlock(EventBus.MapBlockParsedEvent event){

        MapBlock mapblock = event.mapblock;

        //Clear entries for this mapblock
        ctx
                .deleteFrom(SHOP)
                .where(SHOP.POSX.eq(mapblock.x))
                .and(SHOP.POSY.eq(mapblock.y))
                .and(SHOP.POSZ.eq(mapblock.z))
                .and(SHOP.TYPE.eq(TYPE))
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
