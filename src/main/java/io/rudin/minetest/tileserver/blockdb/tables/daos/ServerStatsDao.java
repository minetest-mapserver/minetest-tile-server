/*
 * This file is generated by jOOQ.
*/
package io.rudin.minetest.tileserver.blockdb.tables.daos;


import io.rudin.minetest.tileserver.blockdb.tables.ServerStats;
import io.rudin.minetest.tileserver.blockdb.tables.records.ServerStatsRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ServerStatsDao extends DAOImpl<ServerStatsRecord, io.rudin.minetest.tileserver.blockdb.tables.pojos.ServerStats, String> {

    /**
     * Create a new ServerStatsDao without any configuration
     */
    public ServerStatsDao() {
        super(ServerStats.SERVER_STATS, io.rudin.minetest.tileserver.blockdb.tables.pojos.ServerStats.class);
    }

    /**
     * Create a new ServerStatsDao with an attached configuration
     */
    public ServerStatsDao(Configuration configuration) {
        super(ServerStats.SERVER_STATS, io.rudin.minetest.tileserver.blockdb.tables.pojos.ServerStats.class, configuration);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getId(io.rudin.minetest.tileserver.blockdb.tables.pojos.ServerStats object) {
        return object.getKey();
    }

    /**
     * Fetch records that have <code>key IN (values)</code>
     */
    public List<io.rudin.minetest.tileserver.blockdb.tables.pojos.ServerStats> fetchByKey(String... values) {
        return fetch(ServerStats.SERVER_STATS.KEY, values);
    }

    /**
     * Fetch a unique record that has <code>key = value</code>
     */
    public io.rudin.minetest.tileserver.blockdb.tables.pojos.ServerStats fetchOneByKey(String value) {
        return fetchOne(ServerStats.SERVER_STATS.KEY, value);
    }

    /**
     * Fetch records that have <code>value IN (values)</code>
     */
    public List<io.rudin.minetest.tileserver.blockdb.tables.pojos.ServerStats> fetchByValue(String... values) {
        return fetch(ServerStats.SERVER_STATS.VALUE, values);
    }
}
