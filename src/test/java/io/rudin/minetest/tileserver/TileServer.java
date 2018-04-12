package io.rudin.minetest.tileserver;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.stop;

import java.io.File;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.rudin.minetest.tileserver.cache.FileTileCache;
import io.rudin.minetest.tileserver.cache.TileCache;
import io.rudin.minetest.util.WhiteTile;

public class TileServer {

	public static void main(String[] args) throws Exception {
		
		HikariConfig cfg = new HikariConfig();
		cfg.setUsername("postgres");
		cfg.setPassword("enter");
		cfg.setJdbcUrl("jdbc:postgresql://10.0.0.131:5432/minetest");
		cfg.setDriverClassName("org.postgresql.Driver");

		HikariDataSource dataSource = new HikariDataSource(cfg);

		DSLContext ctx = DSL.using(dataSource, SQLDialect.POSTGRES);
		
		
		TileCache cache = new FileTileCache(new File("target/tiles"));

		ColorTable colorTable = new ColorTable();
		colorTable.load(FetchMap.class.getResourceAsStream("/colors.txt"));
		
		TileRenderer renderer = new TileRenderer(ctx, colorTable);
		
		staticFileLocation("/public");
		init();
		
		
		get("/tiles/:z/:x/:y", (req, res) -> {
			res.header("Content-Type", "image/png");
			int z = Integer.parseInt(req.params(":z"));
			int y = Integer.parseInt(req.params(":y"));
			int x = Integer.parseInt(req.params(":x"));
			
			if (z != 9)
				return WhiteTile.getPNG();
			
			if (cache.has(x, y, z)) {
				return cache.get(x, y, z);
				
			} else {
				byte[] tile = renderer.render(x, y, z);
				
				cache.put(x, y, z, tile);
				
				return tile;
				
			}
		});
		
		System.in.read();
		stop();
	}
	
}
