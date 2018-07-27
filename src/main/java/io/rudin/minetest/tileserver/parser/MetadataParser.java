package io.rudin.minetest.tileserver.parser;

import io.rudin.minetest.tileserver.MapBlockParser;
import org.jooq.Meta;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * https://github.com/minetest/minetest/blob/master/doc/world_format.txt#L330
 */
public class MetadataParser {

    private static final String INVENTORY_TERMINATOR = "EndInventory";

    private static final String INVENTORY_END = "EndInventoryList";
    private static final String INVENTORY_START = "List";

    public static Metadata parse(byte[] metadata, int length){

        Metadata md = new Metadata();


        if (length <= 1){
            //no data to parse
            return md;
        }


        int offset = 0;

        //doc says =1, actual =2!
        int version = metadata[offset];

        if (version != 2){
            throw new IllegalArgumentException("Metadata version incompatible: " + version);
        }

        offset++;
        int count = MapBlockParser.readU16(metadata, offset);

        offset+=2;

        for (int i=0; i<count; i++){
            int position = MapBlockParser.readU16(metadata, offset);
            offset+=2;

            Map<String, String> map = md.map.get(position);
            if (map == null){
                map = new HashMap<>();
                md.map.put(position, map);
            }

            long valuecount = MapBlockParser.readU32(metadata, offset);
            offset+=4;

            for (int j=0; j<valuecount; j++){
                int keyLength = MapBlockParser.readU16(metadata, offset);
                offset+=2;

                String key = new String(metadata, offset, keyLength);

                offset+=keyLength;

                long valueLength = MapBlockParser.readU32(metadata, offset);
                offset+=4;

                String value = new String(metadata, offset, (int)valueLength);

                map.put(key, value);

                offset+=valueLength;
                offset++; //undocumented!

            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(metadata, offset, length - offset)));

            Map<String, Inventory> inventoryMap = md.inventories.get(position);
            if (inventoryMap == null){
                inventoryMap = new HashMap<>();
                md.inventories.put(position, inventoryMap);
            }

            String currentInventoryName = null;
            Inventory currentInventory = null;

            while (true){

                try {
                    String line = reader.readLine();

                    offset += line.length() + 1;

                    if (line.startsWith(INVENTORY_START)){
                        String[] parts = line.split("[ ]");

                        currentInventoryName = parts[1];
                        currentInventory = new Inventory();

                        inventoryMap.put(currentInventoryName, currentInventory);

                        if (parts.length >= 3){
                            currentInventory.size = Integer.parseInt(parts[2]);
                        }

                    } else if (line.equals(INVENTORY_END)){
                        currentInventory = null;
                        currentInventoryName = null;
                    } else if (currentInventory != null) {
                        //content

                        Item item = new Item();
                        currentInventory.items.add(item);

                        if (line.startsWith("Item")) {
                            String[] parts = line.split("[ ]");

                            if (parts.length >= 2)
                                item.name = parts[1];

                            if (parts.length >= 3)
                                item.count = Integer.parseInt(parts[2]);

                            if (parts.length >= 4)
                                item.wear = Integer.parseInt(parts[3]);
                        }
                    } else if (line.equals(INVENTORY_TERMINATOR)){
                            break;

                    } else {
                        throw new IllegalArgumentException("malformed inventory: " + line);

                    }
                    /*
                    List foo 4
                    Item default:sapling
                    Item default:sword_stone 1 10647
                    Item default:dirt 99
                    Empty
                    EndInventoryList
                     */



                } catch (IOException e) {
                    throw new IllegalArgumentException("read-inventory", e);
                }
            }

        }

        return md;

    }

}
