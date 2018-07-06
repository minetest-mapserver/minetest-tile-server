package io.rudin.minetest.tileserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

    public static void main(String[] args) throws Exception {

        TileServerConfig cfg = ConfigFactory.create(TileServerConfig.class);


        Injector injector = Guice.createInjector(
                new ConfigModule(cfg),
                new DBModule(cfg),
                new ServiceModule(cfg)
        );
        DBMigration dbMigration = injector.getInstance(DBMigration.class);
        dbMigration.migrate();

        TileRenderer renderer = injector.getInstance(TileRenderer.class);

        final int x = 0, y = 0, zoom = 10;


        long start = System.currentTimeMillis();
        byte[] tile = renderer.render(x,y,zoom);

        long diff = System.currentTimeMillis() - start;

        logger.info("Rendering took {} ms and produced {} bytes", diff, tile.length);

    }

}
