package io.rudin.minetest.tileserver;

import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.Database;
import org.jooq.util.jaxb.Generate;
import org.jooq.util.jaxb.Generator;
import org.jooq.util.jaxb.Jdbc;
import org.jooq.util.jaxb.Target;

public class CodeGen {

	public static void main(String[] args) throws Exception {
		Configuration cfg = new Configuration();
		
		Jdbc jdbc = new Jdbc();
		jdbc.setDriver("org.postgresql.Driver");
		jdbc.setPassword("enter");
		jdbc.setUsername("postgres");
		jdbc.setUrl("jdbc:postgresql://10.0.0.147:5432/minetest");
		cfg.setJdbc(jdbc);
	
		Generator generator = new Generator();
		cfg.setGenerator(generator);
		
		Database database = new Database();
		database.setName("org.jooq.util.postgres.PostgresDatabase");
		database.setIncludes("blocks|player|tileserver_block_changes|tileserver_block_depth|tileserver_tiles");
		database.setInputSchema("public");
		database.setOutputSchemaToDefault(true);
		generator.setDatabase(database);
		
		Generate generate = new Generate();
		generate.setPojos(true);
		generate.setDaos(true);
		generator.setGenerate(generate);
		
		Target target = new Target();
		target.setDirectory("src/main/java");
		target.setPackageName("io.rudin.minetest.tileserver.blockdb");
		generator.setTarget(target);
		
		GenerationTool.generate(cfg);
		
	}

}
