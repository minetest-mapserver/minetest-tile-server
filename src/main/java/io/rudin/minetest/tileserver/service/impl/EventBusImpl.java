package io.rudin.minetest.tileserver.service.impl;

import com.google.common.eventbus.AsyncEventBus;
import io.rudin.minetest.tileserver.TileServer;
import io.rudin.minetest.tileserver.blockdb.tables.Player;
import io.rudin.minetest.tileserver.service.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

@Singleton
public class EventBusImpl implements EventBus {

    @Inject
    public EventBusImpl(ExecutorService executor){
        eventBus = new AsyncEventBus(executor);
    }

    private final com.google.common.eventbus.EventBus eventBus;

    @Override
    public void post(Object obj) {
        eventBus.post(obj);
    }

    @Override
    public void register(Object listener) {
        eventBus.register(listener);
    }
}
