package io.rudin.minetest.tileserver.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({
	"file:./tileserver.properties"
})
public interface TileServerConfig extends Config {

	@Key("http.port")
	@DefaultValue("8080")
	int httPort();

	//in map blocks
	@Key("tiles.maxy")
	@DefaultValue("10")
	int tilesMaxY();

	@Key("tiles.miny")
	@DefaultValue("-1")
	int tilesMinY();

	@Key("tilerenderer.processes")
	@DefaultValue("8")
	int tilerendererProcesses();

	@Key("tilerenderer.updateinterval")
	@DefaultValue("20")
	int tilerendererUpdateInterval();

	@Key("player.updateinterval")
	@DefaultValue("2")
	int playerUpdateInterval();

	@Key("tiles.directory")
	@DefaultValue("target/tiles")
	String tileDirectory();

	/*
	 Default minetest db
	 */

	@Key("minetest.db.url")
	@DefaultValue("jdbc:postgresql://127.0.0.1:5432/minetest")
	String minetestDatabaseUrl();

	@Key("minetest.db.username")
	@DefaultValue("postgres")
	String minetestDatabaseUsername();
	
	@Key("minetest.db.password")
	@DefaultValue("enter")
	String minetestDatabasePassword();
	
	@Key("minetest.db.driver")
	@DefaultValue("org.postgresql.Driver")
	String minetestDatabaseDriver();

	/*
	 Tile cache DB
	 */

	@Key("tile.db.url")
	@DefaultValue("jdbc:postgresql://127.0.0.1:5432/tiles")
	String tileDatabaseUrl();

	@Key("tile.db.username")
	@DefaultValue("postgres")
	String tileDatabaseUsername();

	@Key("tile.db.password")
	@DefaultValue("enter")
	String tileDatabasePassword();

	@Key("tile.db.driver")
	@DefaultValue("org.postgresql.Driver")
	String tileDatabaseDriver();

}
