package io.rudin.minetest.tileserver.service;

import io.rudin.minetest.tileserver.MapBlock;
import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;
import io.rudin.minetest.tileserver.entity.PlayerInfo;

import java.util.HashMap;
import java.util.Map;

public interface EventBus {

    void post(Object obj);

    void register(Object listener);

    class MapBlockParsedEvent {
        public MapBlock mapblock;
    }

    class TileChangedEvent {
        public int x,y,zoom;
        public int mapblockX, mapblockZ;
    }

    class PlayerMovedEvent {
        public PlayerInfo info;
    }

}
