package io.rudin.minetest.tileserver.service;

import io.rudin.minetest.tileserver.blockdb.tables.pojos.Player;

public interface EventBus {

    void post(Object obj);

    void register(Object listener);

    class TileChangedEvent {
        public int x,y,zoom;
        public int mapblockX, mapblockZ;
    }

    class PlayerMovedEvent {
        public Player player;
    }

}
