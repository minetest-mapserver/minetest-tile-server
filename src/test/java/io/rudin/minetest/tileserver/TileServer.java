package io.rudin.minetest.tileserver;

import static spark.Spark.get;
import static spark.Spark.init;
import static spark.Spark.stop;

public class TileServer {

	public static void main(String[] args) throws Exception {
		
		init();
		
		get("/tiles/:z/:x/:y", (req, res) -> {
			
			int z = Integer.parseInt(req.params(":z"));
			int y = Integer.parseInt(req.params(":y"));
			int x = Integer.parseInt(req.params(":x"));
			
			return "TODO";
		});
		
		System.in.read();
		stop();
	}
	
}
