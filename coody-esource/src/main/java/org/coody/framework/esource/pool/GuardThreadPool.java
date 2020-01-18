package org.coody.framework.esource.pool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GuardThreadPool {

	private static final int CORESIZE_NORMAL = 5;
	private static final int MAXCORESIZE = 10;
	private static final int KEEPALIVETIME = 10;
	private static ThreadFactory esourceFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "esource_create_pool_" + r.hashCode());
		}
	};
	public static final ExecutorService ESOURCE_CREATE_POOL = new ThreadPoolExecutor(CORESIZE_NORMAL, MAXCORESIZE,
			KEEPALIVETIME, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(MAXCORESIZE), esourceFactory,
			new ThreadPoolExecutor.DiscardOldestPolicy());

	private static ThreadFactory guardFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "esource_guard_pool_" + r.hashCode());
		}
	};
	public static final ExecutorService ESOURCE_GUARD_POOL = new ThreadPoolExecutor(CORESIZE_NORMAL, MAXCORESIZE,
			KEEPALIVETIME, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(MAXCORESIZE), guardFactory,
			new ThreadPoolExecutor.DiscardOldestPolicy());

}
