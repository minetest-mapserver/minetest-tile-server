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

    private static final String INVENTORY_TEMRINATOR = "EndInventory";


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

            while (true){

                try {
                    String line = reader.readLine();

                    offset += line.length() + 1;

                    //TODO: parse inventory

                    if (line.equals(INVENTORY_TEMRINATOR)){
                        break;
                    }


                } catch (IOException e) {
                    throw new IllegalArgumentException("read-inventory", e);
                }
            }

        }

        return md;

    }

}
