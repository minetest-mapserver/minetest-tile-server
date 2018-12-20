
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

### colors.file
Supply an external colors file supplementary to the builtin tables
Should be a valid filename or empty (no external colors)
* Default: **none**
