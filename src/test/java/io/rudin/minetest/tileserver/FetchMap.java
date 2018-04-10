package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

public class FetchMap {

	public static void main(String[] args) throws Exception {

		HikariConfig cfg = new HikariConfig();
		cfg.setUsername("postgres");
		cfg.setPassword("enter");
		cfg.setJdbcUrl("jdbc:postgresql://10.0.0.131:5432/minetest");
		cfg.setDriverClassName("org.postgresql.Driver");

		HikariDataSource dataSource = new HikariDataSource(cfg);

		DSLContext ctx = DSL.using(dataSource, SQLDialect.POSTGRES);

		int restrict_min_y = 0;
		int restrict_max_y = 4;
				
		Condition y_restriction = BLOCKS.POSY.ge(restrict_min_y).and(BLOCKS.POSY.le(restrict_max_y));

		Record6<Integer, Integer, Integer, Integer, Integer, Integer> result = ctx
				.select(DSL.min(BLOCKS.POSX), DSL.max(BLOCKS.POSX),
						DSL.min(BLOCKS.POSY), DSL.max(BLOCKS.POSY),
						DSL.min(BLOCKS.POSZ), DSL.max(BLOCKS.POSZ))
				.from(BLOCKS)
				.where(y_restriction)
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
		int block_count = 0;

		System.out.println("Flat blocks: " + flat_blocks);
		
		long start = System.currentTimeMillis();

		for (int blockx = minx; blockx < maxx; blockx++) {
			
			minz = ctx
					.select(DSL.min(BLOCKS.POSZ))
					.from(BLOCKS)
					.where(BLOCKS.POSX.eq(blockx).and(y_restriction))
					.fetchOne(DSL.min(BLOCKS.POSZ));
			
			maxz = ctx
					.select(DSL.max(BLOCKS.POSZ))
					.from(BLOCKS)
					.where(BLOCKS.POSX.eq(blockx).and(y_restriction))
					.fetchOne(DSL.max(BLOCKS.POSZ));
			
			
			for (int blockz = minz; blockz < maxz; blockz++) {

				System.out.println("x=" + blockx + " z=" + blockz);
				
				Result<BlocksRecord> blocks = ctx
					.selectFrom(BLOCKS)
					.where(
							BLOCKS.POSX.eq(blockx)
							.and(BLOCKS.POSZ.eq(blockz))
							.and(y_restriction)
							)
					.fetch();

				for (BlocksRecord block: blocks) {
					MapBlock mapBlock = MapBlockParser.parse(block.getData());
					block_count++;
					
					if (mapBlock.isEmpty())
						continue;
				}
				
			}
		}
		
		long diff = System.currentTimeMillis() - start;
		
		System.out.println("Fetching of " + block_count + " blocks took " + diff + " ms");

		dataSource.close();
	}

}
