package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class FetchMap {

	public static void main(String[] args) throws Exception {

		HikariConfig cfg = new HikariConfig();
		cfg.setUsername("postgres");
		cfg.setPassword("enter");
		cfg.setJdbcUrl("jdbc:postgresql://10.0.0.131:5432/minetest");
		cfg.setDriverClassName("org.postgresql.Driver");

		HikariDataSource dataSource = new HikariDataSource(cfg);

		DSLContext ctx = DSL.using(dataSource, SQLDialect.POSTGRES);

		Record6<Integer, Integer, Integer, Integer, Integer, Integer> result = ctx
				.select(DSL.min(BLOCKS.POSX), DSL.max(BLOCKS.POSX),DSL.min(BLOCKS.POSY), DSL.max(BLOCKS.POSY),DSL.min(BLOCKS.POSZ), DSL.max(BLOCKS.POSZ))
				.from(BLOCKS)
				.fetchOne();

		Integer minx = result.get(DSL.min(BLOCKS.POSX));
		Integer maxx = result.get(DSL.max(BLOCKS.POSX));
		Integer miny = result.get(DSL.min(BLOCKS.POSY));
		Integer maxy = result.get(DSL.max(BLOCKS.POSY));
		Integer minz = result.get(DSL.min(BLOCKS.POSZ));
		Integer maxz = result.get(DSL.max(BLOCKS.POSZ));

		System.out.println("x= " + minx + " to " + maxx);
		System.out.println("y= " + miny + " to " + maxy);
		System.out.println("z= " + minz + " to " + maxz);


		int x_extent = maxx - minx;
		int z_extent = maxz - minz;
		
		int flat_blocks = x_extent * z_extent;
		
		System.out.println("Flat blocks: " + flat_blocks);
		
		dataSource.close();
	}

}
