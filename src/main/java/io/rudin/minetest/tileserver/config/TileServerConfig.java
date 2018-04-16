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
	
	@Key("tiles.directory")
	@DefaultValue("target/tiles")
	String tileDirectory();
	
	@Key("minetest.db.url")
	@DefaultValue("jdbc:postgresql://10.0.0.144:5432/minetest")
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
