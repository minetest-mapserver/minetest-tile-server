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
	
	private File getFile(int layerId, int x, int y, int z, boolean mkParentDirs) {

		File dir = new File(baseDirectory, "" + z); // zoom 1-13

		dir = new File(dir, "" + layerId); // layerID subdir
		dir = new File(dir, "" + x % 1000); // x subdir
		dir = new File(dir, "" + x); // x actual

		dir = new File(dir, "" + y % 1000); // y subdir

		if (!dir.isDirectory() && mkParentDirs) {
			dir.mkdirs();
		}

		return new File(dir, "" + y); // y actual
	}
	
	@Override
	public void put(int layerId, int x, int y, int z, byte[] data) throws IOException {
		StreamUtil.copyStream(new ByteArrayInputStream(data), new FileOutputStream(getFile(layerId, x, y, z, true)));
		timestampMarker.setLastModified(System.currentTimeMillis());
	}

	@Override
	public byte[] get(int layerId, int x, int y, int z) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		File file = getFile(layerId, x, y, z, false);
		if (!file.isFile())
			return null;

		StreamUtil.copyStream(new FileInputStream(file), output);
		
		return output.toByteArray();
	}

	@Override
	public boolean has(int layerId, int x, int y, int z) {
		return getFile(layerId, x, y, z, false).isFile();
	}

	@Override
	public void remove(int layerId, int x, int y, int z) {
		getFile(layerId, x, y, z, false).delete();
	}

	@Override
	public long getLatestTimestamp() {
		return timestampMarker.lastModified();
	}

	@Override
	public void close() {

	}

}
