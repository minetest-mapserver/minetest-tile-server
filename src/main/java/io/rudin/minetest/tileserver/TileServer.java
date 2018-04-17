package io.rudin.minetest.tileserver;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;
import static spark.Spark.stop;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.job.UpdateChangedTilesJob;
import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import io.rudin.minetest.tileserver.route.PlayerRoute;
import io.rudin.minetest.tileserver.route.TileRoute;
import io.rudin.minetest.tileserver.transformer.JsonTransformer;

public class TileServer {

	private static Injector injector;
	
	public static void main(String[] args) throws Exception {
		
		Injector injector = Guice.createInjector(
				new ConfigModule(),
				new DBModule(),
				new ServiceModule()
		);
		
		TileServerConfig cfg = injector.getInstance(TileServerConfig.class);
		
		DBMigration dbMigration = injector.getInstance(DBMigration.class);
		dbMigration.migrate();
		
		staticFileLocation("/public");
		port(cfg.httPort());
		init();
		
		JsonTransformer json = injector.getInstance(JsonTransformer.class);
		
		get("/tiles/:z/:x/:y", injector.getInstance(TileRoute.class));
		get("/player", injector.getInstance(PlayerRoute.class), json);
		
		ScheduledExecutorService executor = injector.getInstance(ScheduledExecutorService.class);
		UpdateChangedTilesJob tilesJob = injector.getInstance(UpdateChangedTilesJob.class);
		
		executor.scheduleAtFixedRate(tilesJob, 0, 2, TimeUnit.SECONDS);
		
		System.in.read();
		
		stop();
		
		//TODO
		executor.shutdown();
		
	}
	
}
