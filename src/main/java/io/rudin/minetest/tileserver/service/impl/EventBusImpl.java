package io.rudin.minetest.tileserver.service.impl;

import io.rudin.minetest.tileserver.TileServer;
import io.rudin.minetest.tileserver.blockdb.tables.Player;
import io.rudin.minetest.tileserver.service.EventBus;

import javax.inject.Singleton;

@Singleton
public class EventBusImpl implements EventBus {

    private final com.google.common.eventbus.EventBus eventBus = new com.google.common.eventbus.EventBus();

    @Override
    public void post(Object obj) {

        if (obj instanceof PlayerMovedEvent){
            PlayerMovedEvent e = (PlayerMovedEvent)obj;
            System.out.println("Player-move: " + e.player.getName() + " @" + e.player.getPosx() + "/" + e.player.getPosz());
        }

        if (obj instanceof TileChangedEvent){
            //TileChangedEvent e = (TileChangedEvent)obj;
            //System.out.println("Mapblock changed: " + e.mapblockX + "/" + e.mapblockZ + "  (Coordinates: " + e.mapblockX*16 + "/" + e.mapblockZ*16 + ") @ zoom " + e.zoom);
        }

        eventBus.post(obj);
    }

    @Override
    public void register(Object listener) {
        eventBus.register(listener);
    }
}
