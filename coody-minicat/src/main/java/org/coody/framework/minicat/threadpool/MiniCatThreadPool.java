package org.coody.framework.minicat.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.coody.framework.minicat.config.MiniCatConfig;

public class MiniCatThreadPool {
	
	
	public static final ExecutorService  HTTP_POOL =  new ThreadPoolExecutor(100,MiniCatConfig.httpThread,
	          10,TimeUnit.SECONDS,
	          new LinkedBlockingQueue<Runnable>()); 
	
	public static final ExecutorService  MINICAT_POOL =  new ThreadPoolExecutor(5,MiniCatConfig.minicatThread,
	          10,TimeUnit.SECONDS,
	          new LinkedBlockingQueue<Runnable>()); 
	
}
