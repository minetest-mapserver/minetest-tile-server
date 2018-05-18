package io.rudin.minetest.tileserver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.zip.DataFormatException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rudin.minetest.tileserver.ColorTable.Color;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

@Singleton
public class MapBlockRenderer {

	private static final Logger logger = LoggerFactory.getLogger(MapBlockRenderer.class);

	private static final int BLOCK_SIZE = 16;
	
	@Inject
	public MapBlockRenderer(ColorTable colorTable) {
		this.colorTable = colorTable;
	}

	private final ColorTable colorTable;

	public void render(Result<BlocksRecord> mapblocks, Graphics graphics) throws IllegalArgumentException, DataFormatException {
		render(mapblocks, graphics, 1);
	}

	public void render(Result<BlocksRecord> mapblocks, Graphics graphics, int scale) throws IllegalArgumentException, DataFormatException {

		int foundBlocks = 0;
		final int expectedBlocks = 16 * 16;

		boolean[][] xz_coords = new boolean[16][16];

		for (BlocksRecord block: mapblocks) {
			MapBlock mapBlock = MapBlockParser.parse(block.getData());

			logger.debug("Checking blocky: {}", block.getPosy());

			if (mapBlock.isEmpty())
				continue;

			for (int x=0; x<16; x++) {
				for (int z=0; z<16; z++) {
					for (int y=15; y>=0; y--) {

						if (xz_coords[x][z]) {
							break;
						}


						Optional<String> node = mapBlock.getNode(x, y, z);

						if (!node.isPresent()) {
							continue;
						}

						String name = node.get();

						Color color = colorTable.getColorMap().get(name);

						if (color != null) {
							logger.debug("Found node '{}' @ {}/{}/{} in blocky: {}", name, x, y, z, block.getPosy());

							graphics.setColor(new java.awt.Color(color.r, color.g, color.b));
							int graphicX = x;
							int graphicY = 15 - z;
							
							graphics.fillRect(graphicX * scale, graphicY * scale, scale, scale);

							xz_coords[x][z] = true;
							foundBlocks++;
							
							if (foundBlocks == expectedBlocks)
								//All done
								return;
							
						} else {
							logger.debug("Color for name '{}' @ {}/{}/{} not found!", name, x, y, z);
							//TODO: color not found
						}

					}
				}

			}

		}

		if (foundBlocks != expectedBlocks) {
			logger.debug("Only found {} blocks in {} layers!", foundBlocks, mapblocks.size());
		}
	}

}
