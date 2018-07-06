package io.rudin.minetest.tileserver.module;

import org.aeonbits.owner.ConfigFactory;

import com.google.inject.AbstractModule;

import io.rudin.minetest.tileserver.config.TileServerConfig;

public class ConfigModule extends AbstractModule {

	public ConfigModule(TileServerConfig cfg){
		this.cfg = cfg;
	}

	private final TileServerConfig cfg;

	@Override
	protected void configure() {
		bind(TileServerConfig.class).toInstance(cfg);
	}

}
