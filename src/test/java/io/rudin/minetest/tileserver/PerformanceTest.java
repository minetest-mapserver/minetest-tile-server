package io.rudin.minetest.tileserver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import io.rudin.minetest.tileserver.service.NoOpCache;
import io.rudin.minetest.tileserver.service.TileCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

public class PerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

    private static Injector injector = Guice.createInjector(
            new ConfigModule(),
            new DBModule(),
            new ServiceModule(NoOpCache.class)
    );

    public static void main(String[] args) throws ExecutionException, IOException, DataFormatException {

        TileServerConfig cfg = injector.getInstance(TileServerConfig.class);

        DBMigration dbMigration = injector.getInstance(DBMigration.class);
        dbMigration.migrate();

        TileRenderer renderer = injector.getInstance(TileRenderer.class);
        NoOpCache cache = injector.getInstance(NoOpCache.class);

        final int x = 0, y = 0, zoom = 10;


        long start = System.currentTimeMillis();
        byte[] tile = renderer.render(x,y,zoom);

        long diff = System.currentTimeMillis() - start;

        logger.info("Rendering took {} ms and produced {} bytes", diff, tile.length);
        logger.info("Cache put-count: {}", cache.getPutCount());


    }

}
