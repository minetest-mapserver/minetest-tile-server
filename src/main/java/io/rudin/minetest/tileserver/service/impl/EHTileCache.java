package io.rudin.minetest.tileserver.service.impl;

import io.rudin.minetest.tileserver.config.TileServerConfig;
import io.rudin.minetest.tileserver.service.TileCache;
import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Singleton
public class EHTileCache implements TileCache {

    @Inject
    public EHTileCache(TileServerConfig cfg){
        this.cfg = cfg;
        File tileDir = new File(cfg.tileDirectory());
        if (!tileDir.isDirectory())
            tileDir.mkdirs();

        this.timestampMarker = new File(tileDir, "timestampmarker-ehcache");

        if (!timestampMarker.isFile()){
            try (OutputStream output = new FileOutputStream(timestampMarker)){
                output.write(0x00);
            } catch (Exception e){
                throw new IllegalArgumentException("could not create timestamp marker!", e);
            }

            timestampMarker.setLastModified(0);
        }


        persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(new File(cfg.tileDirectory(), "ehcache")))
                .withCache("cache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, byte[].class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder()
                                        .heap(10, MemoryUnit.MB)
                                        .disk(100, MemoryUnit.GB, true)
                        )
                )
                .build(true);

        cache = persistentCacheManager.getCache("cache", String.class, byte[].class);
    }

    private final PersistentCacheManager persistentCacheManager;

    private final File timestampMarker;

    private final TileServerConfig cfg;

    private final Cache<String, byte[]> cache;

    private String getKey(int x, int y, int z){
        return x + "/" + y + "/" + z;
    }


    @Override
    public void put(int x, int y, int z, byte[] data) throws IOException {
        cache.put(getKey(x,y,z), data);
        timestampMarker.setLastModified(System.currentTimeMillis());
    }

    @Override
    public byte[] get(int x, int y, int z) throws IOException {
        return cache.get(getKey(x,y,z));
    }

    @Override
    public boolean has(int x, int y, int z) {
        return cache.containsKey(getKey(x,y,z));
    }

    @Override
    public void remove(int x, int y, int z) {
        cache.remove(getKey(x,y,z));
    }

    @Override
    public long getLatestTimestamp() {
        return timestampMarker.lastModified();
    }

    @Override
    public void close() {
        persistentCacheManager.close();
    }
}
