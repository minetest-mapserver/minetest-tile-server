package io.rudin.minetest.tileserver.parser;

import io.rudin.minetest.tileserver.MapBlockParser;
import org.jooq.Meta;

import java.util.HashMap;
import java.util.Map;

public class MetadataParser {

    private static final String INVENTORY_TEMRINATOR = "EndInventory\n";

    private static boolean isInventoryTerminator(byte[] data, int offset){
        for (int i=0; i<INVENTORY_TEMRINATOR.length(); i++){
            if (data[offset+i] != INVENTORY_TEMRINATOR.charAt(i))
                return false;
        }

        return true;
    }

    public static Metadata parse(byte[] metadata, int length){

        Metadata md = new Metadata();

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

            while (offset < length - INVENTORY_TEMRINATOR.length()){
                //search end inv marker

                if (isInventoryTerminator(metadata, offset)){
                    break;
                }

                offset++;
            }

        }

        return md;

    }

}
