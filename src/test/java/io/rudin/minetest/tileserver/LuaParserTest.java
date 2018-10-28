package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.util.LuaParser;
import org.junit.Assert;
import org.junit.Test;
import org.luaj.vm2.LuaTable;

import javax.script.ScriptException;
import java.util.Map;

public class LuaParserTest {


    static final String MAP_STR = "return {[\"admin_vendor\"] = true, [\"depositor\"] = false, [\"split_incoming_stacks\"] = false,\n" +
            " [\"output_item\"] = \"currency:minegeld\", [\"quit\"] = true, [\"accept_worn_input\"] = true, [\"digiline_channel\"] = \"\", [\"output_item_qty\"] = 1,\n" +
            " [\"auto_sort\"] = false, [\"input_item\"] = \"default:coal_lump\", [\"currency_eject\"] = false, [\"accept_output_only\"] = false, [\"co_sellers\"] = \"\",\n" +
            " [\"accept_worn_output\"] = true, [\"banned_buyers\"] = \"\", [\"input_item_qty\"] = 1, [\"inactive_force\"] = false}";

    @Test
    public void testMap() throws ScriptException {

        LuaTable map = LuaParser.parseMap(MAP_STR);

        Assert.assertNotNull(map);
        Assert.assertEquals(true, map.get("admin_vendor").checkboolean());
        Assert.assertEquals("currency:minegeld", map.get("output_item").tojstring());
        Assert.assertEquals(1, map.get("output_item_qty").toint());

    }

}
