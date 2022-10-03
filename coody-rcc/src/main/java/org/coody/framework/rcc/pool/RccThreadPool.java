package org.coody.framework.rcc.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coody.framework.rcc.config.RccConfig;

public class RccThreadPool {

	public static final ExecutorService SERVER_POOL = new ThreadPoolExecutor(100, RccConfig.serverThread, 10,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	public static final ExecutorService CONSUME_POOL = new ThreadPoolExecutor(5, RccConfig.consumeThread, 10,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	public static final ExecutorService GUARD_POOL = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS,
			new LinkedBlockingQueue<Runnable>());

}
