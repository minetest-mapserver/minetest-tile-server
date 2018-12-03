package io.rudin.minetest.tileserver.service;

import io.rudin.minetest.tileserver.util.MapBlock;
import io.rudin.minetest.tileserver.entity.PlayerInfo;

public interface EventBus {

    void post(Object obj);

    void register(Object listener);

    class MapBlockParsedEvent {
        public MapBlock mapblock;
    }

    class TileChangedEvent {
        public int x,y,zoom;
        public int mapblockX, mapblockZ;
        public int layerId;
    }

    class PlayerMovedEvent {
        public PlayerInfo info;
    }

}
