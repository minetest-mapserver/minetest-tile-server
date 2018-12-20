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

See [config](docs/config.md)

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