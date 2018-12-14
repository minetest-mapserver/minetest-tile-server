package io.rudin.minetest.tileserver.module;

import com.google.inject.AbstractModule;
import io.rudin.minetest.tileserver.ColorTable;
import io.rudin.minetest.tileserver.config.LayerConfig;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.provider.ColorTableProvider;
import io.rudin.minetest.tileserver.provider.ExecutorProvider;
import io.rudin.minetest.tileserver.provider.LayerConfigProvider;
import io.rudin.minetest.tileserver.service.*;
import io.rudin.minetest.tileserver.service.impl.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class TestServiceModule  extends AbstractModule {


    @Override
    protected void configure() {

        bind(TileCache.class).to(FileTileCache.class);
        bind(EventBus.class).to(EventBusImpl.class);
        bind(ColorTable.class).toProvider(ColorTableProvider.class);
        bind(ExecutorService.class).toProvider(ExecutorProvider.class);
        bind(ScheduledExecutorService.class).toProvider(ExecutorProvider.class);
        bind(LayerConfig.class).toProvider(LayerConfigProvider.class);
        bind(MapBlockRenderService.class).to(DefaultMapBlockRenderService.class);

        bind(BlocksRecordService.class).to(BlocksRecordDatabaseService.class);
    }
}