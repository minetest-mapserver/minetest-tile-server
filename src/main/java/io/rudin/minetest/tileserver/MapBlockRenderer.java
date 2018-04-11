package io.rudin.minetest.tileserver;

import java.awt.Graphics;
import java.util.Optional;
import java.util.zip.DataFormatException;

import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rudin.minetest.tileserver.ColorTable.Color;
import io.rudin.minetest.tileserver.blockdb.tables.records.BlocksRecord;

public class MapBlockRenderer {

	private static final Logger logger = LoggerFactory.getLogger(MapBlockRenderer.class);

	public MapBlockRenderer(Graphics graphics, ColorTable colorTable) {
		this.graphics = graphics;
		this.colorTable = colorTable;
	}

	private final Graphics graphics;

	private final ColorTable colorTable;

	public void render(Result<BlocksRecord> mapblocks) throws IllegalArgumentException, DataFormatException {


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

					if (xz_coords[x][z]) {
						continue;
					}

					for (int y=15; y>=0; y--) {

						Optional<String> node = mapBlock.getNode(x, y, z);

						if (!node.isPresent()) {
							break;
						}

						String name = node.get();

						Color color = colorTable.getColorMap().get(name);

						if (color != null) {
							logger.debug("Found node '{}' @ {}/{}/{} in blocky: {}", name, x, y, z, block.getPosy());

							graphics.setColor(new java.awt.Color(color.r, color.g, color.b));
							graphics.drawLine(x, z, x, z);

							xz_coords[x][z] = true;
							foundBlocks++;
						} else {
							logger.debug("Color for name '{}' @ {}/{}/{} not found!", name, x, y, z);
							//TODO: color not found
						}

					}

					if (foundBlocks == expectedBlocks) {
						logger.debug("All top blocks found ({}) exiting @ block-y: {}", foundBlocks, block.getPosy());
						return;
					}
				}

			}

		}

		if (foundBlocks != expectedBlocks) {
			logger.debug("Only found {} blocks in {} layers!", foundBlocks, mapblocks.size());
		}

	}

}
