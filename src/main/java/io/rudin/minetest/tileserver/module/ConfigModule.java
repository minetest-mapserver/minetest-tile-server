package io.rudin.minetest.tileserver.module;

import org.aeonbits.owner.ConfigFactory;

import com.google.inject.AbstractModule;

import io.rudin.minetest.tileserver.config.TileServerConfig;

public class ConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TileServerConfig.class).toInstance(ConfigFactory.create(TileServerConfig.class));
	}

}
