package io.rudin.minetest.tileserver.util;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class UnknownBlockCollector {


    private final Map<String, Integer> blockCountMap = new ConcurrentHashMap<>();

    public void add(String blockName){
        //not so pretty code, but works for some indication of unknown block count
        Integer count = blockCountMap.getOrDefault(blockName, 0);
        count++;
        blockCountMap.put(blockName, count);
    }

    public Map<String, Integer> getUnknownBlockCount(){
        return blockCountMap;
    }

}
