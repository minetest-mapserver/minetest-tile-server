package io.rudin.minetest.tileserver.listener;

import com.google.common.eventbus.Subscribe;
import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.records.ShopRecord;
import io.rudin.minetest.tileserver.parser.Inventory;
import io.rudin.minetest.tileserver.parser.Item;
import io.rudin.minetest.tileserver.parser.Metadata;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import io.rudin.minetest.tileserver.service.EventBus;
import io.rudin.minetest.tileserver.util.LuaParser;
import org.jooq.DSLContext;
import org.luaj.vm2.LuaTable;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

import static io.rudin.minetest.tileserver.blockdb.tables.Shop.SHOP;


public class FancyVendBlockListener {


    private static final String ADMIN_VENDOR = "fancy_vend:admin_vendor";
    private static final String ADMIN_DEPO = "fancy_vend:admin_depo";
    private static final String PLAYER_VENDOR = "fancy_vend:player_vendor";
    private static final String PLAYER_DEPO = "fancy_vend:player_depo";

    private static final String TYPE = "Fancyvend";

    @Inject
    public FancyVendBlockListener(EventBus eventBus, @MapDB DSLContext ctx){
        this.eventBus = eventBus;
        this.ctx = ctx;
    }

    private final DSLContext ctx;

    private final EventBus eventBus;

    public void setup(){
        eventBus.register(this);
    }

    /*
    {
owner=admin,
configured=true,
settings=
 return {["admin_vendor"] = true, ["depositor"] = false, ["split_incoming_stacks"] = false,
 ["output_item"] = "currency:minegeld", ["quit"] = true, ["accept_worn_input"] = true, ["digiline_channel"] = "", ["output_item_qty"] = 1,
 ["auto_sort"] = false, ["input_item"] = "default:coal_lump", ["currency_eject"] = false, ["accept_output_only"] = false, ["co_sellers"] = "",
 ["accept_worn_output"] = true, ["banned_buyers"] = "", ["input_item_qty"] = 1, ["inactive_force"] = false},
item=currency:minegeld,
infotext=Admin Vendor trading 1 Coal Lump for 1 1 MineGeld Note (owned by admin),
log=return {"Vendor placed by admin", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player ilai purchased 1 lots from this vendor.", "Player didi1 purchased 1 lots from this vendor.", "Player didi1 purchased 1 lots from this vendor.", "Player LalTilism purchased 1 lots from this vendor."},
message=You have insufficient funds
}


player vendor:
wanted_item=Inventory{size=1, items=[Item{name='currency:minegeld', count=0, wear=0}]}, given_item=Inventory{size=1, items=[Item{name='default:jungletree', count=0, wear=0}
main={size=90}

     */

    private void register(MapBlock mapBlock, int x, int y, int z, boolean isAdmin){
        Metadata metadata = mapBlock.getMetadata();

        int position = MapBlock.toPosition(x, y, z);

        Map<String, String> map = metadata.map.get(position);
        Map<String, Inventory> inv = metadata.inventories.get(position);

        int posx = (mapBlock.x * 16) + x;
        int posy = (mapBlock.y * 16) + y;
        int posz = (mapBlock.z * 16) + z;

        Inventory pay = inv.get("wanted_item");
        Inventory give = inv.get("given_item");
        Inventory main = inv.get("main");

        String settings = map.get("settings");
        LuaTable settingsTable = LuaParser.parseMap(settings);

        if (pay.items.isEmpty() || give.items.isEmpty())
            return;

        String in_item = pay.items.get(0).name;
        int in_count = Math.max(1, settingsTable.get("input_item_qty").toint());

        String out_item = give.items.get(0).name;
        int out_count = Math.max(1, settingsTable.get("output_item_qty").toint());

        if (in_item == null || out_item == null)
            //Nothing to sell yet
            return;

        int stock = 0;

        if (isAdmin){
            stock = 999;

        } else {
            for (Item item: main.items){
                if (item.name.equals(out_item)){
                    stock += item.count;
                }
            }

        }

        //multiples of out_count
        int stock_factor = (int)Math.floor((double)stock / (double)out_count);

        ShopRecord record = ctx.newRecord(SHOP);
        record.setType(TYPE);
        record.setOwner(map.get("owner"));
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
                .deleteFrom(SHOP)
                .where(SHOP.POSX.eq(mapblock.x))
                .and(SHOP.POSY.eq(mapblock.y))
                .and(SHOP.POSZ.eq(mapblock.z))
                .and(SHOP.TYPE.eq(TYPE))
                .execute();

        if (mapblock.mapping.containsValue(ADMIN_VENDOR) || mapblock.mapping.containsValue(PLAYER_VENDOR)){

            for (int x=0; x<16; x++){
                for (int y=0; y<16; y++){
                    for (int z=0; z<16; z++){
                        Optional<String> node = mapblock.getNode(x, y, z);

                        if (node.isPresent() && (node.get().equals(PLAYER_VENDOR) || node.get().equals(PLAYER_DEPO))) {
                            register(mapblock, x, y, z, false);
                        } else if (node.isPresent() && (node.get().equals(ADMIN_VENDOR) || node.get().equals(ADMIN_DEPO))){
                            register(mapblock, x,y,z, true);
                        }
                    }
                }
            }

        }

    }
}
