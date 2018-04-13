package io.rudin.minetest.tileserver;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;
import static spark.Spark.stop;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import io.rudin.minetest.tileserver.route.TileRoute;

public class TileServer {

	private static Injector injector;
	
	public static void main(String[] args) throws Exception {
		
		Injector injector = Guice.createInjector(
				new ConfigModule(),
				new DBModule(),
				new ServiceModule()
		);
		
		TileServerConfig cfg = injector.getInstance(TileServerConfig.class);
		
		staticFileLocation("/public");
		port(cfg.httPort());
		init();
		
		get("/tiles/:z/:x/:y", injector.getInstance(TileRoute.class));
		
		
		System.in.read();
		
		stop();
		
		
	}
	
}
