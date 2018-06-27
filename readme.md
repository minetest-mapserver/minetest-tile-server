Minetest tileserver
=======

Near realtime tileserver for minetest

# Overview

A standalone java-based tile-server for minetest.
Renders the mapblocks on-demand as an interactive map and displays the current players.
As players interact with the map the changed MapBlocks get re-rendered
and updated in the web-client (near-realtime, 20 seconds default)

The resulting tiles are cached in a database and served to the client if unchanged. 

# Development state

* Active, Beta-Testing with one Production-Instance (see **Demo**)
* Please file issues/requests in the github issue-tracker

# Demo

* See: https://pandorabox.io/map/#-1830.125/426.5/11

# Compatibility

Requirements:
- Postgresql Database (for tile-cache and blocks)
- Java runtime (8+)
- Modern machine with enough (2GB+) RAM and CPU

# Installing

* Download the jar in the releases section
* Configure the database connections according to the **Configuration** section if they are not default value
* Start the server with: `java -jar tileserver.jar`

# Configuring

The application drwas its configuration from the `tileserver.properties` file.
Key and values should be separated by a `=` (see **Example config**)

## Example config

Example for a setup with server-name **myhost**:
```
minetest.db.url=jdbc:postgresql://myhost:5432/postgres
tile.db.url=jdbc:postgresql://myhost:5432/tiles
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

### tilerenderer.processes
Number of processes to allow for rendering
* Default: **24**

### tilerenderer.updateinterval
Update interval to check for new tiles (in seconds)
* Default: **20**

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

### tile.db.url
Url for Tile-DB Connection (jdbc connection string)
* Default: **jdbc:postgresql://127.0.0.1:5432/tiles**

### tile.db.username
Username for Tile-DB Connection
* Default: **sa**

### tile.db.password
Username for Tile-DB Connection
* Default:

### tile.db.driver
Driver for Tile-DB Connection (only psql supported for now)
* Default: **org.postgresql.Driver**

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