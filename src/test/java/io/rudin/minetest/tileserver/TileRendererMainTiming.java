package io.rudin.minetest.tileserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.rudin.minetest.tileserver.config.Layer;
import io.rudin.minetest.tileserver.config.LayerConfig;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import io.rudin.minetest.tileserver.service.TileCache;
import org.aeonbits.owner.ConfigFactory;

public class TileRendererMainTiming {

    public static void main(String[] args) throws Exception {

        TileServerConfig cfg = ConfigFactory.create(TileServerConfig.class);


        Injector injector = Guice.createInjector(
                new ConfigModule(cfg),
                new DBModule(cfg),
                new ServiceModule(cfg)
        );
        DBMigration dbMigration = injector.getInstance(DBMigration.class);
        dbMigration.migrate();

        int x = 0;
        int y = 0;
        int zoom = 9; //13;

        LayerConfig layerConfig = injector.getInstance(LayerConfig.class);
        Layer layer = layerConfig.layers.get(0);

        TileRenderer renderer = injector.getInstance(TileRenderer.class);
        TileCache cache = injector.getInstance(TileCache.class);

        cache.remove(0,x,y,zoom);

        long start = System.currentTimeMillis();
        renderer.renderImage(layer,x,y,zoom);
        long diff = System.currentTimeMillis() - start;

        System.out.println("render took " + diff + " ms");



    }
}
