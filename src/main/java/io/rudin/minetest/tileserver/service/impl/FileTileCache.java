package io.rudin.minetest.tileserver.service.impl;

import java.io.*;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.service.TileCache;
import io.rudin.minetest.tileserver.util.StreamUtil;

@Singleton
public class FileTileCache implements TileCache {

	@Inject
	public FileTileCache(TileServerConfig cfg) {
		this.baseDirectory = new File(cfg.tileDirectory());

		if (!this.baseDirectory.isDirectory())
			this.baseDirectory.mkdirs();

		this.timestampMarker = new File(baseDirectory, "timestampmarker");

		if (!timestampMarker.isFile()){
			try (OutputStream output = new FileOutputStream(timestampMarker)){
				output.write(0x00);
			} catch (Exception e){
				throw new IllegalArgumentException("could not create timestamp marker!", e);
			}

			timestampMarker.setLastModified(0);
		}
	}
	
	private final File baseDirectory;

	private final File timestampMarker;
	
	private File getFile(int x, int y, int z) {
		
		File dir = new File(baseDirectory, z + "/" + x);
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}
		
		return new File(dir, "" + y);
	}
	
	@Override
	public void put(int x, int y, int z, byte[] data) throws IOException {
		StreamUtil.copyStream(new ByteArrayInputStream(data), new FileOutputStream(getFile(x, y, z)));
		timestampMarker.setLastModified(System.currentTimeMillis());
	}

	@Override
	public byte[] get(int x, int y, int z) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamUtil.copyStream(new FileInputStream(getFile(x, y, z)), output);
		
		return output.toByteArray();
	}

	@Override
	public boolean has(int x, int y, int z) {
		return getFile(x, y, z).isFile();
	}

	@Override
	public void remove(int x, int y, int z) {
		getFile(x, y, z).delete();
	}

	@Override
	public long getLatestTimestamp() {
		return timestampMarker.lastModified();
	}

}
