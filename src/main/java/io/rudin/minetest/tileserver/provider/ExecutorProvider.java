package io.rudin.minetest.tileserver.provider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ExecutorProvider implements Provider<ScheduledExecutorService> {

	public ExecutorProvider() {
		
		executor = Executors.newScheduledThreadPool(64);
		
		//TODO: war-package-case
		Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
	}
	
	public void stop() {
		executor.shutdownNow();
	}
	
	private final ScheduledExecutorService executor;
	
	@Override
	public ScheduledExecutorService get() {
		return executor;
	}

}
