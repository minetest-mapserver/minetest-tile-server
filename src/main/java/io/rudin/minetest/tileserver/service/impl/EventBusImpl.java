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
        eventBus.post(obj);
    }

    @Override
    public void register(Object listener) {
        eventBus.register(listener);
    }
}
