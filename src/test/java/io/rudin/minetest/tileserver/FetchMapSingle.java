package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

public class FetchMapSingle {

	private static final Logger logger = LoggerFactory.getLogger(FetchMapSingle.class);

	public static void main(String[] args) throws Exception {

		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");

		HikariConfig cfg = new HikariConfig();
		cfg.setUsername("postgres");
		cfg.setPassword("enter");
		cfg.setJdbcUrl("jdbc:postgresql://10.0.0.131:5432/minetest");
		cfg.setDriverClassName("org.postgresql.Driver");

		HikariDataSource dataSource = new HikariDataSource(cfg);

		DSLContext ctx = DSL.using(dataSource, SQLDialect.POSTGRES);

		int blockx = -106;
		int blockz = -11;
		
		int absolute_x = blockx * 16;
		int absolute_z = blockz * 16;
		
		logger.info("Absolute address: x/z = {}/{} to {}/{}", absolute_x, absolute_z, absolute_x+16, absolute_z+16);

		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();

		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, 16, 16);

		ColorTable colorTable = new ColorTable();
		colorTable.load(FetchMapSingle.class.getResourceAsStream("/colors.txt"));

		long start = System.currentTimeMillis();

		Result<BlocksRecord> blocks = ctx
				.selectFrom(BLOCKS)
				.where(
						BLOCKS.POSX.eq(blockx)
						.and(BLOCKS.POSZ.eq(blockz))
						)
				.orderBy(BLOCKS.POSY.desc())
				.fetch();

		Graphics blockGraphics = graphics.create(0, 0, 16, 16);

		MapBlockRenderer renderer = new MapBlockRenderer(blockGraphics, colorTable);
		renderer.render(blocks);


		graphics.dispose();

		ImageIO.write(image, "png", new File("target/output.png"));
		

		long diff = System.currentTimeMillis() - start;

		System.out.println("Fetching took " + diff + " ms");

		dataSource.close();
	}

}
