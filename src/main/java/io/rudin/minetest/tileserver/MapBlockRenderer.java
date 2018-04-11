package io.rudin.minetest.tileserver;

import java.awt.Graphics;
import java.util.Optional;
import java.util.zip.DataFormatException;

import org.jooq.Result;

import io.rudin.minetest.tileserver.ColorTable.Color;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

public class MapBlockRenderer {

	public MapBlockRenderer(Graphics graphics, ColorTable colorTable) {
		this.graphics = graphics;
		this.colorTable = colorTable;
	}
	
	private final Graphics graphics;
	
	private final ColorTable colorTable;
	
	public void render(Result<BlocksRecord> mapblocks) throws IllegalArgumentException, DataFormatException {
		

		int foundBlocks = 0;
		final int expectedBlocks = 16 * 16;
		
		boolean[][] xy_coords = new boolean[16][16];
		
		for (BlocksRecord block: mapblocks) {
			MapBlock mapBlock = MapBlockParser.parse(block.getData());
			
			if (mapBlock.isEmpty())
				continue;
			
			for (int y=15; y>=0; y--) {
				for (int x=0; x<16; x++) {
					for (int z=0; z<16; z++) {
						
						if (xy_coords[x][z]) {
							break;
						}
						
						Optional<String> node = mapBlock.getNode(x, y, z);
						
						if (!node.isPresent()) {
							break;
						}
						
						String name = node.get();
						
						Color color = colorTable.getColorMap().get(name);
						
						if (color != null) {
							
							graphics.setColor(new java.awt.Color(color.r, color.g, color.b));
							graphics.drawLine(x, z, x, z);
							
							xy_coords[x][z] = true;
							foundBlocks++;
						} else {
							
							//TODO: color not found
						}
						
					}
					
					if (foundBlocks == expectedBlocks)
						return;
				}

			}
		
		}
		
	}
	
}
