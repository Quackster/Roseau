package org.alexdev.roseau.pooling;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPooling {

	private NonBlockingExecutor nonBlockingExecutor;
	private ScheduledExecutorService scheduledThreadPool;

	public ThreadPooling() {
		this.scheduledThreadPool = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
		this.nonBlockingExecutor = new NonBlockingExecutor(this.scheduledThreadPool);
	}

	public Future<?> register(Runnable runnable) {
		return this.scheduledThreadPool.submit(runnable);
	}

	public ScheduledExecutorService getScheduled() {
		return this.scheduledThreadPool;
	}

	public NonBlockingExecutor getNonBlockingExecutor() {
		return nonBlockingExecutor;
	}
}