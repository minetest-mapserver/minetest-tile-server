package io.rudin.minetest.tileserver;

import org.flywaydb.core.Flyway;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.*;

public class TileDBCodeGen {

	public static void main(String[] args) throws Exception {
		Configuration cfg = new Configuration();
		
		Jdbc jdbc = new Jdbc();
		jdbc.setDriver("org.postgresql.Driver");
		jdbc.setPassword("enter");
		jdbc.setUsername("postgres");
		jdbc.setUrl("jdbc:postgresql://127.0.0.1:5432/tiles");
		cfg.setJdbc(jdbc);
	
		Generator generator = new Generator();
		cfg.setGenerator(generator);
		
		Database database = new Database();
		database.setName("org.jooq.util.postgres.PostgresDatabase");
		database.setInputSchema("public");
		database.setOutputSchemaToDefault(true);
		generator.setDatabase(database);
		
		Generate generate = new Generate();
		generate.setPojos(true);
		generate.setDaos(true);
		generator.setGenerate(generate);
		
		Target target = new Target();
		target.setDirectory("src/main/java");
		target.setPackageName("io.rudin.minetest.tileserver.tiledb");
		generator.setTarget(target);

		Flyway flyway = new Flyway();
		flyway.setSqlMigrationPrefix("TILEV");
		flyway.setDataSource(jdbc.getUrl(), jdbc.getUsername(), jdbc.getPassword());
		flyway.migrate();

		GenerationTool.generate(cfg);
		
	}

}
