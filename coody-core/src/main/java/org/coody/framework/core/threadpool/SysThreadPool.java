package org.coody.framework.core.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 系统线程池，用于多线程，异步线程处理统一管理
 * @author Coody
 * @date 2018年10月31日
 */
public class SysThreadPool {
	
	private static final int CORESIZE_NORMAL=20;
	private static final int MAXCORESIZE = 100;
	private static final int KEEPALIVETIME = 10;  //10s
	private static  ThreadFactory threadFactory = new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "sys_pool_" + r.hashCode());
		}
	};
	public static final ExecutorService  THREAD_POOL =new ThreadPoolExecutor(CORESIZE_NORMAL, MAXCORESIZE, KEEPALIVETIME, TimeUnit.MILLISECONDS,
			new ArrayBlockingQueue<Runnable>(MAXCORESIZE), threadFactory,new ThreadPoolExecutor.DiscardOldestPolicy());
	
	
}
