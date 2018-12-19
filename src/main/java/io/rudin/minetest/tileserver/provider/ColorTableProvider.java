package io.rudin.minetest.tileserver.provider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.ColorTable;
import io.rudin.minetest.tileserver.config.TileServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;

@Singleton
public class ColorTableProvider implements Provider<ColorTable> {

	@Inject
	public ColorTableProvider(TileServerConfig cfg){
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	private static final Logger logger = LoggerFactory.getLogger(ColorTableProvider.class);

	@Override
	@Singleton
	public ColorTable get() {
		ColorTable colorTable = new ColorTable();
		colorTable.load(ColorTableProvider.class.getResourceAsStream("/colors.txt"));
		colorTable.load(ColorTableProvider.class.getResourceAsStream("/vanessa.txt"));

		String externalColorsFile = cfg.externalColorsFile();
		if (externalColorsFile != null){
			logger.info("Loading colors from external file: '{}'", externalColorsFile);
			try (InputStream input = new FileInputStream(externalColorsFile)){
				colorTable.load(input);

			} catch (Exception e){
				logger.warn("external colors", e);

			}

		}

		return colorTable;
	}

}
