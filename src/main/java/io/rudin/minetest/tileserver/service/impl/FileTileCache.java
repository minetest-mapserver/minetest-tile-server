package io.rudin.minetest.tileserver.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
	}
	
	private final File baseDirectory;
	
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

}
