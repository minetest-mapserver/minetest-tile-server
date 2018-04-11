package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

public class FetchMap {
	
	private static final Logger logger = LoggerFactory.getLogger(FetchMap.class);

	public static void main(String[] args) throws Exception {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
		
		HikariConfig cfg = new HikariConfig();
		cfg.setUsername("postgres");
		cfg.setPassword("enter");
		cfg.setJdbcUrl("jdbc:postgresql://10.0.0.131:5432/minetest");
		cfg.setDriverClassName("org.postgresql.Driver");

		HikariDataSource dataSource = new HikariDataSource(cfg);

		DSLContext ctx = DSL.using(dataSource, SQLDialect.POSTGRES);

		int restrict_min_y = -500;
		int restrict_max_y = 1500;
				
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
		
		int x_blocks = x_extent * 16;
		int z_blocks = z_extent * 16;
		
		int flat_block_count = x_blocks * z_blocks;
		int block_count = 0;

		System.out.println("Flat blocks: " + flat_block_count);
		
		BufferedImage image = new BufferedImage(x_blocks, z_blocks, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, x_blocks, z_blocks);
		
		ColorTable colorTable = new ColorTable();
		colorTable.load(FetchMap.class.getResourceAsStream("/colors.txt"));
		
		long start = System.currentTimeMillis();

		for (int blockx = minx; blockx < maxx; blockx++) {
			
			int iter_minz = ctx
					.select(DSL.min(BLOCKS.POSZ))
					.from(BLOCKS)
					.where(BLOCKS.POSX.eq(blockx).and(y_restriction))
					.fetchOne(DSL.min(BLOCKS.POSZ));
			
			int iter_maxz = ctx
					.select(DSL.max(BLOCKS.POSZ))
					.from(BLOCKS)
					.where(BLOCKS.POSX.eq(blockx).and(y_restriction))
					.fetchOne(DSL.max(BLOCKS.POSZ));
			
			
			for (int blockz = iter_minz; blockz < iter_maxz; blockz++) {
				
				System.out.println("x=" + blockx + " z=" + blockz);
				
				Result<BlocksRecord> blocks = ctx
					.selectFrom(BLOCKS)
					.where(
							BLOCKS.POSX.eq(blockx)
							.and(BLOCKS.POSZ.eq(blockz))
							.and(y_restriction)
							)
					.orderBy(BLOCKS.POSY.desc())
					.fetch();
				
				if (blocks.isEmpty())
					continue;
				
				int graphics_offset_x = (blockx - minx) * 16;
				int graphics_offset_z = (blockz - minz) * 16;
				
				Graphics blockGraphics = graphics.create(graphics_offset_x, graphics_offset_z, 16, 16);
				
				MapBlockRenderer renderer = new MapBlockRenderer(blockGraphics, colorTable);
				renderer.render(blocks);
				
				
			}
		}
		
		graphics.dispose();
		
		ImageIO.write(image, "png", new File("target/output.png"));


		long diff = System.currentTimeMillis() - start;
		
		System.out.println("Fetching of " + block_count + " mapblocks took " + diff + " ms");

		dataSource.close();
	}
	
}
