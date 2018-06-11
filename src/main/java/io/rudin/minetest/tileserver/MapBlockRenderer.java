package io.rudin.minetest.tileserver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.util.MapBlockAccessor;
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
	public MapBlockRenderer(ColorTable colorTable, MapBlockAccessor mapBlockAccessor) {
		this.colorTable = colorTable;
		this.mapBlockAccessor = mapBlockAccessor;
	}

	private final MapBlockAccessor mapBlockAccessor;

	private final ColorTable colorTable;

	private int safeColorComponent(int value){
		if (value > 255)
			return 255;
		if (value < 0)
			return 0;
		return value;
	}

	private java.awt.Color addAlpha(java.awt.Color color, int value){

		int red = safeColorComponent(color.getRed() + value);
		int green = safeColorComponent(color.getGreen() + value);
		int blue = safeColorComponent(color.getBlue() + value);

		return new java.awt.Color(red,green,blue);
	}

	public void render(int x, int z, Graphics graphics) throws IllegalArgumentException, DataFormatException, ExecutionException {
		render(x, z, graphics, 1);
	}

	public void render(int mapBlockX, int mapBlockZ, Graphics graphics, int scale) throws IllegalArgumentException, DataFormatException, ExecutionException {

		int foundBlocks = 0;
		final int expectedBlocks = 16 * 16;

		boolean[][] xz_coords = new boolean[16][16];

		List<MapBlock> mapblocks = mapBlockAccessor.getTopDownYStride(mapBlockX, mapBlockZ);

		for (MapBlock mapBlock: mapblocks) {

			logger.debug("Checking blocky: {}", mapBlock.y);

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
							logger.debug("Found node '{}' @ {}/{}/{} in blocky: {}", name, x, y, z, mapBlock.y);

							//get left node
							Optional<String> left = Optional.empty();
							Optional<String> leftAbove = Optional.empty();

							//top node
							Optional<String> top = Optional.empty();
							Optional<String> topAbove = Optional.empty();

							if (x > 0){
								//same mapblock
								left = mapBlock.getNode(x-1, y, z);
								leftAbove = mapBlock.getNode(x-1, y+1, z);
							} else {
								//neighbouring mapblock
								MapBlock leftMapBlock = mapBlockAccessor.get(mapBlockX - 1, mapBlock.y, mapBlockZ);
								if (leftMapBlock != null) {
									left = leftMapBlock.getNode(15, y, z);
									leftAbove = leftMapBlock.getNode(15, y + 1, z);
								}
							}

							if (z < 14){
								//same mapblock
								top = mapBlock.getNode(x, y, z+1);
								topAbove = mapBlock.getNode(x, y+1, z+1);
							} else {
								//neighbouring mapblock
								MapBlock leftMapBlock = mapBlockAccessor.get(mapBlockX, mapBlock.y, mapBlockZ+1);
								if (leftMapBlock != null) {
									top = leftMapBlock.getNode(x, y, 0);
									topAbove = leftMapBlock.getNode(x, y + 1, 0);
								}
							}



							int graphicX = x;
							int graphicY = 15 - z;

							java.awt.Color pixelColor = new java.awt.Color(color.r, color.g, color.b);

							if (leftAbove.isPresent())
								pixelColor = addAlpha(pixelColor, -5);

							if (topAbove.isPresent())
								pixelColor = addAlpha(pixelColor, -5);

							if (!left.isPresent())
								pixelColor = addAlpha(pixelColor, 5);

							if (!top.isPresent())
								pixelColor = addAlpha(pixelColor, 5);

							graphics.setColor(pixelColor);
							graphics.fillRect(graphicX * scale, graphicY * scale, scale, scale);

							/*
							if (!left.isPresent()){
								//no node to the left or top
								graphics.setColor(pixelColor.brighter());
								graphics.fillRect(graphicX * scale, graphicY * scale, 8, scale);
							}

							if (!top.isPresent()){
								//no node to the left or top
								graphics.setColor(pixelColor.brighter());
								graphics.fillRect(graphicX * scale, graphicY * scale, scale, 8);
							}

							if (leftAbove.isPresent()){
								//node casts shadow
								graphics.setColor(pixelColor.darker());
								graphics.fillRect(graphicX * scale, graphicY * scale, 8, scale);
							}

							if (topAbove.isPresent()){
								//node casts shadow
								graphics.setColor(pixelColor.darker());
								graphics.fillRect(graphicX * scale, graphicY * scale, scale, 8);
							}
							*/


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
