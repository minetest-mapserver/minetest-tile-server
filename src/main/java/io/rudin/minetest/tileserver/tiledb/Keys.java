/*
 * This file is generated by jOOQ.
*/
package io.rudin.minetest.tileserver.tiledb;


import io.rudin.minetest.tileserver.tiledb.tables.FlywaySchemaHistory;
import io.rudin.minetest.tileserver.tiledb.tables.Tiles;
import io.rudin.minetest.tileserver.tiledb.tables.records.FlywaySchemaHistoryRecord;
import io.rudin.minetest.tileserver.tiledb.tables.records.TilesRecord;

import javax.annotation.Generated;

import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code></code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<FlywaySchemaHistoryRecord> FLYWAY_SCHEMA_HISTORY_PK = UniqueKeys0.FLYWAY_SCHEMA_HISTORY_PK;
    public static final UniqueKey<TilesRecord> TILES_PKEY = UniqueKeys0.TILES_PKEY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<FlywaySchemaHistoryRecord> FLYWAY_SCHEMA_HISTORY_PK = Internal.createUniqueKey(FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY, "flyway_schema_history_pk", FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY.INSTALLED_RANK);
        public static final UniqueKey<TilesRecord> TILES_PKEY = Internal.createUniqueKey(Tiles.TILES, "tiles_pkey", Tiles.TILES.X, Tiles.TILES.Y, Tiles.TILES.Z);
    }
}