package io.rudin.minetest.tileserver;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.stop;

import java.io.File;

import io.rudin.minetest.tileserver.cache.FileTileCache;
import io.rudin.minetest.tileserver.cache.TileCache;

public class TileServer {

	public static void main(String[] args) throws Exception {
		
		TileCache cache = new FileTileCache(new File("target/tiles"));

		//TODO: populate
		
		staticFileLocation("/public");
		init();
		
		
		get("/tiles/:z/:x/:y", (req, res) -> {
			res.header("Content-Type", "image/png");
			
			int z = Integer.parseInt(req.params(":z"));
			int y = Integer.parseInt(req.params(":y"));
			int x = Integer.parseInt(req.params(":x"));
			
			if (cache.has(x, y, z)) {
				return cache.get(x, y, z);
				
			} else {
				//Compute
				//TODO
			}
			
			return new byte[] {};
		});
		
		System.in.read();
		stop();
	}
	
}
