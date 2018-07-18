Minetest tileserver
=======

Near realtime tileserver for minetest

# Overview

A standalone java-based tile-server for minetest.
Renders the mapblocks on-demand as an interactive map and displays the current players.
As players interact with the map the changed MapBlocks get re-rendered
and updated in the web-client (near-realtime, 20 seconds default)

The resulting tiles are cached in a file-backend and served to the client if unchanged.

# Development state

* Tested and live with one Production-Instance (see **Demo**)
* Please file issues/requests in the github issue-tracker

# Demo

* See: https://pandorabox.io/map/#-1830.125/426.5/11
![](img/Bildschirmfoto_2018-07-18_17-13-35.png?raw=true)

# Compatibility

Requirements:
- Postgresql Database for minetest backend (existing)
- Java runtime (8+)
- Modern machine with enough (2GB+) RAM and CPU

Testing was done on a fairly new minetest version (0.4.17)
There **will** be failures in the MapBlock Parser if the version found is not met (older minetest-versions).

Please create an issue with minetest-version and MapBlock info in the bug-tracker if that happens to you.

# Installing

* Download the jar in the releases section
* **Backup** your minetest database (**THIS IS IMPORTANT!!**) as the db schema gets updated (see **How it works**)
* Configure the database connection according to the **Configuration** section if they are not default value
* Start the server with: `java -jar tileserver.jar`

# Configuring

The application drwas its configuration from the `tileserver.properties` file.
Key and values should be separated by a `=` (see **Example config**)

## Example config

Example for a setup with server-name **myhost**:
```
#tileserver.properties
minetest.db.url=jdbc:postgresql://myhost:5432/postgres
minetest.db.username=postgres
minetest.db.password=1234
```

## Configuration parameters

### http.port
Port to expose http server on
* Default: **8080**

### tiles.maxy
Max y value for blocks to render (in Mapblocks)
* Default: **10** (Equals 160 blocks)

### tiles.miny
Min y value for blocks to render (in Mapblocks)
* Default: **-1** (Equals -16 blocks)

### tilerenderer.updateinterval
Update interval to check for new tiles (in seconds)
* Default: **20**

### tilerenderer.initialrendering.enable
Enable initial rendering, renders/caches all tiles on startup and disables the realtime tile-update until restarted with false again.
Leaving this to default (false) renders all tiles on-demand
* Default: **false**

### player.updateinterval
Update interval to check for Player movements (in seconds)
* Default: **2**

### minetest.db.url
Url for DB Connection (jdbc connection string)
* Default: **jdbc:postgresql://127.0.0.1:5432/minetest**

### minetest.db.username
Username for DB Connection
* Default: **sa**

### minetest.db.password
Username for DB Connection
* Default:

### minetest.db.driver
Driver for DB Connection (only psql supported for now)
* Default: **org.postgresql.Driver**

### block.parser.poi.enable
Enable parsing of POI blocks, provided with the mod in this repository
* Default: **true**

### block.parser.travelnet.enable
Enable parsing of Travelnet boxes
* Default: **true**


# How it works

The **blocks** table in the minetest database gets a new column for the modification time.
Triggers for `insert` and `update` on the table ensure the time gets updated after each change: 

```sql
alter table blocks add column mtime bigint not null default 0;
create index BLOCKS_TIME on blocks(mtime);

create or replace function on_blocks_change() returns trigger as
$BODY$
BEGIN
    NEW.mtime = floor(EXTRACT(EPOCH from now()) * 1000);
    return NEW;
END;
$BODY$
LANGUAGE plpgsql;

create trigger blocks_update
 before insert or update
 on blocks
 for each row
 execute procedure on_blocks_change();

```
**These changes get applied atomatically with the initial DB-Migration, so please create a backup before starting the Tileserver!!**

With this information and the modification-time on the created tiles the
periodically scheduled Updater-Job removes the stale Tiles in the tile-cache.

On the next access (browsing on the web-interface to the coordinates) the Tile gets re-rendered.


# Building

Requirements:
* Java 8+
* Maven 3+

Build, test, install:
```
mvn clean install
```

## With Docker

* See the `Makefile`

# Contributing

* Issues, recommendations and pull-requests are welcome