package io.rudin.minetest.tileserver;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.accessor.Coordinate;
import io.rudin.minetest.tileserver.accessor.MapBlockAccessor;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Singleton
public class MapBlockRenderer {

	private static final Logger logger = LoggerFactory.getLogger(MapBlockRenderer.class);

	private static final int BLOCK_SIZE = 16;
	
	@Inject
	public MapBlockRenderer(ColorTable colorTable, MapBlockAccessor mapBlockAccessor, TileServerConfig cfg) {
		this.colorTable = colorTable;
		this.mapBlockAccessor = mapBlockAccessor;
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	private final MapBlockAccessor mapBlockAccessor;

	private final ColorTable colorTable;

	public void render(int x, int z, Graphics graphics) throws IllegalArgumentException, DataFormatException, ExecutionException {
		render(x, z, graphics, 1);
	}

	public void render(int mapBlockX, int mapBlockZ, Graphics graphics, int scale) throws IllegalArgumentException, DataFormatException, ExecutionException {

		logger.debug("Rendering block: x={} z={}", mapBlockX, mapBlockZ);

		int foundBlocks = 0;
		final int expectedBlocks = 16 * 16;

		boolean[][] xz_coords = new boolean[16][16];

		mapBlockAccessor.prefetchTopDownYStride(mapBlockX, mapBlockZ, cfg.tilesMinY(), cfg.tilesMaxY());

		for (int blocky = cfg.tilesMaxY(); blocky>=cfg.tilesMinY(); blocky--){
			Optional<MapBlock> optional = mapBlockAccessor.get(new Coordinate(mapBlockX, blocky, mapBlockZ));

			if (!optional.isPresent())
				continue;

			MapBlock mapBlock = optional.get();

			logger.trace("Checking blocky: {}", mapBlock.y);

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

						ColorTable.RGBData rgb = colorTable.getColorMap().get(name);

						if (rgb != null) {

							//working copy
							rgb = new ColorTable.RGBData(rgb);

							logger.trace("Found node '{}' @ {}/{}/{} in blocky: {}", name, x, y, z, mapBlock.y);

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
								Optional<MapBlock> leftMapBlock = mapBlockAccessor.get(new Coordinate(mapBlockX - 1, mapBlock.y, mapBlockZ));
								if (leftMapBlock.isPresent()) {
									left = leftMapBlock.get().getNode(15, y, z);
									leftAbove = leftMapBlock.get().getNode(15, y + 1, z);
								}
							}

							if (z < 14){
								//same mapblock
								top = mapBlock.getNode(x, y, z+1);
								topAbove = mapBlock.getNode(x, y+1, z+1);
							} else {
								//neighbouring mapblock
								Optional<MapBlock> leftMapBlock = mapBlockAccessor.get(new Coordinate(mapBlockX, mapBlock.y, mapBlockZ+1));
								if (leftMapBlock.isPresent()) {
									top = leftMapBlock.get().getNode(x, y, 0);
									topAbove = leftMapBlock.get().getNode(x, y + 1, 0);
								}
							}



							int graphicX = x;
							int graphicY = 15 - z;

							if (leftAbove.isPresent())
								rgb.addComponent(-5);

							if (topAbove.isPresent())
								rgb.addComponent(-5);

							if (!left.isPresent())
								rgb.addComponent(5);

							if (!top.isPresent())
								rgb.addComponent(5);

							graphics.setColor(rgb.toColor());
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
			logger.debug("Only found {} blocks", foundBlocks);
		}
	}

}
