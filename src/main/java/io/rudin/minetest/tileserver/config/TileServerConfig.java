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
	@DefaultValue("4")
	int tilerendererProcesses();

	@Key("tiles.directory")
	@DefaultValue("target/tiles")
	String tileDirectory();
	
	@Key("minetest.db.url")
	@DefaultValue("jdbc:postgresql://127.0.0.1:5432/postgres")
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
	
}
