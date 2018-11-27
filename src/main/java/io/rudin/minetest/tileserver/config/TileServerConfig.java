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

	/*
	Tile renderer stuff
	 */

	@Key("prometheus.enable")
	@DefaultValue("false")
	boolean prometheusEnable();

	@Key("prometheus.port")
	@DefaultValue("8081")
	int prometheusPort();

	//This should only be enabled once after a fresh install
	@Key("tilerenderer.initialrendering.enable")
	@DefaultValue("false")
	boolean tilerendererEnableInitialRendering();

	@Key("tilerenderer.updateinterval")
	@DefaultValue("20")
	int tilerendererUpdateInterval();

	@Key("tilerenderer.maxupdateblocks")
	@DefaultValue("500")
	int tilerendererUpdateMaxBlocks();

	@Key("player.updateinterval")
	@DefaultValue("2")
	int playerUpdateInterval();

	/*
	Logging stuff
	 */

	@Key("log.query.performance")
	@DefaultValue("false")
	boolean logQueryPerformance();

	@Key("log.tile.updatetimings")
	@DefaultValue("false")
	boolean logTileUpdateTimings();

	/*
	block parsing stuff
	 */

	@Key("block.parser.poi.enable")
	@DefaultValue("true")
	boolean parserPoiEnable();

	@Key("block.parser.travelnet.enable")
	@DefaultValue("true")
	boolean parserTravelnetEnable();

	@Key("block.parser.missions.enable")
	@DefaultValue("true")
	boolean parserMissionsEnable();

	@Key("block.parser.smartshop.enable")
	@DefaultValue("true")
	boolean parserSmartshopEnable();

	@Key("block.parser.fancyvend.enable")
	@DefaultValue("true")
	boolean parserFancyVendEnable();

    @Key("block.parser.protector.enable")
    @DefaultValue("true")
    boolean parserProtectorEnable();

    @Key("block.parser.train.enable")
    @DefaultValue("true")
    boolean parserTrainEnable();

	/*
	Static web files
	 */

	@Key("static.files.location")
	@DefaultValue("")
	String staticFilesLocation();

	/*
	Tile rendering strategy
	 */

	@Key("tile.rendering.strategy")
	@DefaultValue("ASAP")
	TileRenderingStrategy tileRenderingStrategy();

	enum TileRenderingStrategy {
		//When tiles are viewed in browser
		ON_DEMAND,

		//When tiles change
		ASAP
	}

	/*
	tile cache stuff
	 */

	@Key("tile.cache.impl")
	@DefaultValue("FILE")
	CacheType tileCacheType();

	enum CacheType {
		DATABASE,
		FILE,
		EHCACHE
	}

	@Key("tiles.directory")
	@DefaultValue("tiles")
	String tileDirectory();

	/*
	 Matomo tracker id
	 */
	@Key("tracker.matomo.id")
	String matomoTrackerId();

	@Key("tracker.matomo.url")
	String matomoTrackerUrl();

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

	/*
	Debug stuff
	 */

	@Key("debug.enable")
	@DefaultValue("false")
	boolean enableDebug();

	@Key("debug.mapblock.dumpfailed")
	@DefaultValue("false")
	boolean dumpFailedMapblocks();

	/**
	 * Saves mapblocks as local file if enabled
	 * @return
	 */
	@Key("debug.mapblock.save")
	@DefaultValue("false")
	boolean saveMapBlocks();

}
