package io.rudin.minetest.tileserver;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.job.UpdateChangedTilesJob;
import io.rudin.minetest.tileserver.job.UpdatePlayerJob;
import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import io.rudin.minetest.tileserver.route.PlayerRoute;
import io.rudin.minetest.tileserver.route.TileRoute;
import io.rudin.minetest.tileserver.transformer.JsonTransformer;
import io.rudin.minetest.tileserver.ws.WebSocketHandler;
import io.rudin.minetest.tileserver.ws.WebSocketUpdater;

import static spark.Spark.*;

public class TileServer {

	private static Injector injector = Guice.createInjector(
			new ConfigModule(),
			new DBModule(),
			new ServiceModule()
	);
	
	public static void main(String[] args) throws Exception {

		TileServerConfig cfg = injector.getInstance(TileServerConfig.class);
		
		DBMigration dbMigration = injector.getInstance(DBMigration.class);
		dbMigration.migrate();
		
		staticFileLocation("/public");
		port(cfg.httPort());

		JsonTransformer json = injector.getInstance(JsonTransformer.class);

		webSocket("/ws", WebSocketHandler.class);
		get("/tiles/:z/:x/:y", injector.getInstance(TileRoute.class));
		get("/player", injector.getInstance(PlayerRoute.class), json);

		//Initialize web server
		init();

		//Initialize ws updater
		injector.getInstance(WebSocketUpdater.class).init();

		ScheduledExecutorService executor = injector.getInstance(ScheduledExecutorService.class);

		executor.scheduleAtFixedRate(injector.getInstance(UpdateChangedTilesJob.class), 0, 2, TimeUnit.SECONDS);
		executor.scheduleAtFixedRate(injector.getInstance(UpdatePlayerJob.class), 0, 1, TimeUnit.SECONDS);

		AtomicBoolean running = new AtomicBoolean(true);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			stop();
			executor.shutdownNow();
			running.set(false);
		}));

		while (running.get()){
			Thread.sleep(500);
		}
		
	}
	
}
