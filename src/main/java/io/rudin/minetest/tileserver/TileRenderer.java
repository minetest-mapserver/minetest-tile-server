package io.rudin.minetest.tileserver;

import static io.rudin.minetest.tileserver.blockdb.tables.Blocks.BLOCKS;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import org.jooq.DSLContext;
import org.jooq.Result;

import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;
import io.rudin.minetest.util.CoordinateResolver;
import io.rudin.minetest.util.CoordinateResolver.MapBlockCoordinateInfo;

public class TileRenderer {

	public TileRenderer(DSLContext ctx, ColorTable colorTable) {
		this.ctx = ctx;
		this.colorTable = colorTable;
	}
	
	private final DSLContext ctx;
	
	private final ColorTable colorTable;
	
	public byte[] render(int tileX, int tileY, int zoom) throws IllegalArgumentException, DataFormatException, IOException {
		

		MapBlockCoordinateInfo coordinateInfo = CoordinateResolver.fromTile(tileX, tileY, 9);
		
		//16x16 mapblocks on a tile
		BufferedImage image = new BufferedImage(CoordinateResolver.TILE_PIXEL_SIZE, CoordinateResolver.TILE_PIXEL_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, CoordinateResolver.TILE_PIXEL_SIZE, CoordinateResolver.TILE_PIXEL_SIZE);
		
		for (int mbx=0; mbx<16; mbx++) {
			for (int mbz=0; mbz<16; mbz++) {
				
				Graphics mapblockGraphics = graphics.create(mbx * CoordinateResolver.MAPBLOCK_PIXEL_SIZE,
						mbz * CoordinateResolver.MAPBLOCK_PIXEL_SIZE,
						CoordinateResolver.MAPBLOCK_PIXEL_SIZE,
						CoordinateResolver.MAPBLOCK_PIXEL_SIZE);

				int mapblockX = coordinateInfo.x + mbx;
				int mapblockZ = coordinateInfo.z + (mbz *-1);
								
				Result<BlocksRecord> blocks = ctx
					.selectFrom(BLOCKS)
					.where(
							BLOCKS.POSX.eq(mapblockX)
							.and(BLOCKS.POSZ.eq(mapblockZ))
							)
					.orderBy(BLOCKS.POSY.desc())
					.fetch();
				
				if (blocks.isEmpty())
					continue;
				
				MapBlockRenderer renderer = new MapBlockRenderer(mapblockGraphics, colorTable);
				
				renderer.render(blocks);
			}
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image, "png", output);
		
		return output.toByteArray();
	}
	
}
