package org.coody.framework.logged.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoggedThreadPool {

	public static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

}
