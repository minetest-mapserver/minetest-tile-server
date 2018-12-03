package io.rudin.minetest.tileserver.base;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import io.rudin.minetest.tileserver.DBMigration;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.module.ConfigModule;
import io.rudin.minetest.tileserver.module.DBModule;
import io.rudin.minetest.tileserver.module.ServiceModule;
import io.rudin.minetest.tileserver.module.TestServiceModule;
import io.rudin.minetest.tileserver.qualifier.MapDB;
import org.aeonbits.owner.ConfigFactory;
import org.junit.After;
import org.junit.Before;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TileServerTest {

    @Before
    public void init() throws SQLException {
        Map<String, String> properties = new HashMap<>();
        properties.put("minetest.db.url", "jdbc:h2:mem:tileserver");
        properties.put("minetest.db.username", "sa");
        properties.put("minetest.db.password", "");
        properties.put("minetest.db.driver", "org.h2.Driver");

        TileServerConfig cfg = ConfigFactory.create(TileServerConfig.class, properties);

        injector = Guice.createInjector(
                new ConfigModule(cfg),
                new DBModule(cfg),
                new TestServiceModule()
        );

        DataSource dataSource = injector.getInstance(Key.get(DataSource.class, MapDB.class));
        try (Connection connection = dataSource.getConnection()){
            connection.createStatement().execute("drop all objects");
            connection.createStatement().execute("runscript from 'classpath:/minetest-db.sql'");
        }

        //does not work: create function plsql not available in h2
        //injector.getInstance(DBMigration.class).migrate();

        injector.injectMembers(this);
    }

    private Injector injector;

    @After
    public void after(){
    }

}
